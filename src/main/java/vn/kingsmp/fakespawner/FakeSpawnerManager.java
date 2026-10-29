package vn.kingsmp.fakespawner;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Quan ly danh sach cac Khoi Dong dang duoc gia lam Long trieu hoi Skeleton,
 * hieu ung HUD +$ 53.76K / +EXP 512, va khoi phuc stack khi dap block.
 */
public class FakeSpawnerManager {
    private static final Map<BlockPos, FakeSpawnerData> SPAWNERS = new ConcurrentHashMap<>();

    // HUD popup ben trai phia tren khung chat sau khi nhan EXP (00:18 - 00:31 trong video)
    private static long hudDisplayUntilMs = 0L;
    private static int lastClaimedExp = 512;
    private static String lastMoneyText = "53.76K";

    // Vi tri vua dap de tu dong gom item Copper Block roi ra tren mat dat
    private static BlockPos recentlyBrokenPos = null;
    private static long recentlyBrokenUntilMs = 0L;

    public static FakeSpawnerData get(BlockPos pos) {
        return SPAWNERS.get(pos);
    }

    public static FakeSpawnerData getOrCreateDefault(BlockPos pos) {
        return SPAWNERS.computeIfAbsent(pos.toImmutable(), p -> new FakeSpawnerData(p, 256, -1, 0));
    }

    public static Collection<FakeSpawnerData> getAll() {
        return SPAWNERS.values();
    }

    public static void onPlaceFirst(ClientPlayerEntity player, BlockPos placedPos, int slotIndex, int initialStackSize) {
        // Khi dat khoi dong dau tien, server chi tru 1 item.
        // Client an 63 item con lai trong o hotbar de hien thi nhu da dat ca stack 64 long!
        int hiddenRemainder = Math.max(0, initialStackSize - 1);
        FakeSpawnerData data = new FakeSpawnerData(placedPos, initialStackSize, slotIndex, hiddenRemainder);
        SPAWNERS.put(placedPos.toImmutable(), data);

        if (hiddenRemainder > 0) {
            ItemStack slotStack = player.getInventory().getStack(slotIndex);
            if (slotStack.isOf(Items.COPPER_BLOCK)) {
                slotStack.setCount(0);
            }
        }
    }

    public static void onStackMore(ClientPlayerEntity player, World world, BlockPos targetPos, int slotIndex, ItemStack heldStack) {
        int added = heldStack.getCount();
        if (added <= 0) return;

        FakeSpawnerData data = SPAWNERS.get(targetPos);
        if (data == null) {
            // Neu khoi dong nay da co san truoc do, coi nhu ban dau la 64
            data = new FakeSpawnerData(targetPos, 64, -1, 0);
            SPAWNERS.put(targetPos.toImmutable(), data);
        }

        data.addStack(added, slotIndex, added);
        heldStack.setCount(0);

        world.playSound(player, targetPos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);

        // Hien thi Action Bar giong het KingSMP (00:08 - 00:11):
        // "Bạn đã đặt thêm [64] Lồng triệu hồi Skeleton , tổng [256]"
        Text msg = Text.empty()
                .append(Text.literal("Bạn đã đặt thêm ").formatted(Formatting.YELLOW))
                .append(Text.literal("[" + added + "] ").formatted(Formatting.WHITE))
                .append(Text.literal("Lồng triệu hồi Skeleton ").formatted(Formatting.AQUA))
                .append(Text.literal(", tổng ").formatted(Formatting.YELLOW))
                .append(Text.literal("[" + data.stackCount + "]").formatted(Formatting.WHITE));
        player.sendMessage(msg, true);
    }

    public static void onBreakSpawner(ClientPlayerEntity player, World world, BlockPos brokenPos) {
        FakeSpawnerData data = SPAWNERS.remove(brokenPos);
        int brokenCount = (data != null) ? data.stackCount : 256;

        // Hien thi Action Bar giong het KingSMP (00:21):
        // "Bạn đã đập [256] Lồng triệu hồi Skeleton , Còn lại 0"
        Text msg = Text.empty()
                .append(Text.literal("Bạn đã đập ").formatted(Formatting.YELLOW))
                .append(Text.literal("[" + brokenCount + "] ").formatted(Formatting.WHITE))
                .append(Text.literal("Lồng triệu hồi Skeleton ").formatted(Formatting.AQUA))
                .append(Text.literal(", Còn lại ").formatted(Formatting.YELLOW))
                .append(Text.literal("0").formatted(Formatting.RED));
        player.sendMessage(msg, true);

        // Khoi phuc toan bo cac stack 64 ve lai hotbar nhu trong video (00:21)
        if (data != null) {
            for (FakeSpawnerData.SlotRecord rec : data.consumedSlots) {
                if (rec.slotIndex() >= 0 && rec.slotIndex() < player.getInventory().size()) {
                    ItemStack existing = player.getInventory().getStack(rec.slotIndex());
                    if (existing.isEmpty()) {
                        player.getInventory().setStack(rec.slotIndex(), new ItemStack(Items.COPPER_BLOCK, 64));
                    } else if (existing.isOf(Items.COPPER_BLOCK)) {
                        existing.setCount(64);
                    }
                }
            }
        }

        recentlyBrokenPos = brokenPos.toImmutable();
        recentlyBrokenUntilMs = System.currentTimeMillis() + 3000L;
    }

    public static void triggerExpClaimHud(ClientPlayerEntity player, int claimedExp) {
        lastClaimedExp = claimedExp;
        // 512 EXP -> +$ 53.76K nhu trong video 00:18
        double moneyK = (claimedExp / 512.0) * 53.76;
        lastMoneyText = String.format(java.util.Locale.US, "%.2fK", moneyK);
        hudDisplayUntilMs = System.currentTimeMillis() + 14000L;

        // Action Bar: +EXP 512 (00:18)
        player.sendMessage(Text.literal("+EXP " + claimedExp).formatted(Formatting.GREEN), true);
        player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 0.7F, 1.2F);
    }

    public static boolean isHudActive() {
        return System.currentTimeMillis() < hudDisplayUntilMs;
    }

    public static int getLastClaimedExp() {
        return lastClaimedExp;
    }

    public static String getLastMoneyText() {
        return lastMoneyText;
    }

    public static void clientTick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;

        // Kiem tra neu khoi dong bi pha vo (do dao xong tren server)
        for (BlockPos pos : SPAWNERS.keySet()) {
            if (!client.world.getBlockState(pos).isOf(Blocks.COPPER_BLOCK)) {
                onBreakSpawner(client.player, client.world, pos);
            }
        }

        // Dam bao cac slot hotbar vua khoi phuc giu nguyen 64 khi nhat lai 1 khoi dong roi ra tu server
        if (recentlyBrokenPos != null && System.currentTimeMillis() < recentlyBrokenUntilMs) {
            Box box = new Box(recentlyBrokenPos).expand(3.0);
            for (Entity entity : client.world.getEntitiesByClass(ItemEntity.class, box, e -> true)) {
                ItemEntity itemEntity = (ItemEntity) entity;
                if (itemEntity.getStack().isOf(Items.COPPER_BLOCK)) {
                    itemEntity.discard();
                }
            }
        }
    }

    public static Text getFakeSpawnerName() {
        return Text.literal("Lồng triệu hồi Skeleton")
                .setStyle(Style.EMPTY.withColor(Formatting.AQUA).withItalic(false));
    }
}
