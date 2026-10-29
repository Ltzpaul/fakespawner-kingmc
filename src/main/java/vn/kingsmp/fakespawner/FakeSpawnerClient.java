package vn.kingsmp.fakespawner;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class FakeSpawnerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 1. Cho phep Khoi Dong (Copper Block) hien thi trong suot (cutout) nhu Long trieu hoi
        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.COPPER_BLOCK, RenderLayer.getCutout());

        // 2. Them 2 dong Lore giong het trong Shulker Box (00:01):
        //    "Interact with Spawn Egg:" (Xam)
        //    "Sets Mob Type" (Vang cam)
        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (stack.isOf(Items.COPPER_BLOCK)) {
                if (!lines.isEmpty()) {
                    lines.set(0, FakeSpawnerManager.getFakeSpawnerName());
                }
                lines.add(Text.literal("Interact with Spawn Egg:")
                        .setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)));
                lines.add(Text.literal("Sets Mob Type")
                        .setStyle(Style.EMPTY.withColor(Formatting.GOLD).withItalic(false)));
            }
        });

        // 3. Xu ly chuot phai vao Khoi Dong:
        //    - Neu dang cam Khoi Dong (Long trieu hoi): Cong don stack (+64 -> 128 -> 192 -> 256) va hien Action Bar
        //    - Neu cam Cuoc / tay khong: Mo giao dien 6 hang "256 Skeleton"
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient() || hand != Hand.MAIN_HAND) {
                return ActionResult.PASS;
            }

            BlockPos hitPos = hitResult.getBlockPos();
            BlockState hitState = world.getBlockState(hitPos);
            ItemStack heldStack = player.getStackInHand(hand);
            int selectedSlot = player.getInventory().selectedSlot;

            // Truong hop click thang vao Long trieu hoi (Khoi Dong) da dat
            if (hitState.isOf(Blocks.COPPER_BLOCK)) {
                if (heldStack.isOf(Items.COPPER_BLOCK)) {
                    // Gop them stack vao long trieu hoi hien tai (00:08 - 00:10)
                    FakeSpawnerManager.onStackMore(
                            MinecraftClient.getInstance().player,
                            world,
                            hitPos,
                            selectedSlot,
                            heldStack
                    );
                    return ActionResult.SUCCESS;
                } else {
                    // Mo GUI "256 Skeleton" (00:14)
                    FakeSpawnerData data = FakeSpawnerManager.getOrCreateDefault(hitPos);
                    MinecraftClient.getInstance().setScreen(
                            new FakeSpawnerScreen(data, player.getInventory())
                    );
                    return ActionResult.SUCCESS;
                }
            }

            // Truong hop dat Long trieu hoi dau tien xuong mat dat (00:06)
            if (heldStack.isOf(Items.COPPER_BLOCK)) {
                BlockPos placePos = hitPos.offset(hitResult.getSide());
                int initialCount = heldStack.getCount();
                MinecraftClient.getInstance().execute(() -> {
                    if (MinecraftClient.getInstance().player != null) {
                        FakeSpawnerManager.onPlaceFirst(
                                MinecraftClient.getInstance().player,
                                placePos,
                                selectedSlot,
                                initialCount
                        );
                    }
                });
            }

            return ActionResult.PASS;
        });

        // 4. Tao hat khoi & lua xung quanh Long trieu hoi + Kiem tra trang thai moi tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FakeSpawnerManager.clientTick(client);

            if (client.world != null && client.player != null) {
                BlockPos playerPos = client.player.getBlockPos();
                Random random = client.world.random;
                for (BlockPos pos : BlockPos.iterate(playerPos.add(-12, -6, -12), playerPos.add(12, 6, 12))) {
                    if (client.world.getBlockState(pos).isOf(Blocks.COPPER_BLOCK)) {
                        if (random.nextFloat() < 0.35F) {
                            double x = pos.getX() + random.nextDouble();
                            double y = pos.getY() + random.nextDouble();
                            double z = pos.getZ() + random.nextDouble();
                            client.world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.0, 0.0);
                            client.world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0);
                        }
                    }
                }
            }
        });

        // 5. Hien thi HUD "+$ 53.76K" va "+EXP 512" o goc trai phia tren khung chat (00:18 - 00:31)
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (FakeSpawnerManager.isHudActive()) {
                MinecraftClient client = MinecraftClient.getInstance();
                int screenHeight = client.getWindow().getScaledHeight();
                int x = 8;
                int y = screenHeight - 52;

                Text moneyLine = Text.literal("+$ " + FakeSpawnerManager.getLastMoneyText())
                        .formatted(Formatting.GREEN);
                Text expLine = Text.literal("+EXP " + FakeSpawnerManager.getLastClaimedExp())
                        .formatted(Formatting.GREEN);

                drawContext.drawTextWithShadow(client.textRenderer, moneyLine, x, y, 0x55FF55);
                drawContext.drawTextWithShadow(client.textRenderer, expLine, x, y + 10, 0x55FF55);
            }
        });
    }
}
