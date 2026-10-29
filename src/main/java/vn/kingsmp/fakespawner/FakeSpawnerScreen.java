package vn.kingsmp.fakespawner;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Giao dien 6 hang (9x6) "256 Skeleton" tai hien chinh xac 100% khung hinh 00:14 - 00:18 trong video.
 */
public class FakeSpawnerScreen extends HandledScreen<GenericContainerScreenHandler> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/generic_54.png");
    private final FakeSpawnerData spawnerData;
    private final SimpleInventory containerInv;

    public FakeSpawnerScreen(FakeSpawnerData spawnerData, PlayerInventory playerInventory) {
        super(
                new GenericContainerScreenHandler(
                        ScreenHandlerType.GENERIC_9X6,
                        0,
                        playerInventory,
                        new SimpleInventory(54),
                        6
                ),
                playerInventory,
                Text.literal(spawnerData.stackCount + " Skeleton")
        );
        this.spawnerData = spawnerData;
        this.containerInv = (SimpleInventory) this.handler.getInventory();
        this.backgroundHeight = 114 + 6 * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        populateInventory();
    }

    private void populateInventory() {
        this.containerInv.clear();

        // Cac o 0..44: Chua Xuong (64 xuong moi o, voi 256 Skeleton -> 8 o dau = 512 xuong nhu video 00:14)
        int remainingBones = this.spawnerData.boneCount;
        int slot = 0;
        while (remainingBones > 0 && slot < 45) {
            int stackSize = Math.min(64, remainingBones);
            this.containerInv.setStack(slot, new ItemStack(Items.BONE, stackSize));
            remainingBones -= stackSize;
            slot++;
        }

        // Hang cuoi (Slots 45..53):
        // Slot 45: Mui ten (Arrow)
        ItemStack arrow = new ItemStack(Items.ARROW);
        arrow.set(DataComponentTypes.CUSTOM_NAME, noItalic(Text.literal("Trang trước").formatted(Formatting.YELLOW)));
        this.containerInv.setStack(45, arrow);

        // Slots 46, 47, 51, 52: Kinh mau xam (Filler panes)
        ItemStack filler = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
        filler.set(DataComponentTypes.CUSTOM_NAME, Text.literal(" "));
        this.containerInv.setStack(46, filler.copy());
        this.containerInv.setStack(47, filler.copy());
        this.containerInv.setStack(51, filler.copy());
        this.containerInv.setStack(52, filler.copy());

        // Slot 48: Bundle (Tui)
        ItemStack bundle = new ItemStack(Items.BUNDLE);
        bundle.set(DataComponentTypes.CUSTOM_NAME, noItalic(Text.literal("BÁN VẬT PHẨM").formatted(Formatting.GOLD)));
        this.containerInv.setStack(48, bundle);

        // Slot 49 va Slot 50: Chai kinh nghiem (NHẬN EXP) & Long trieu hoi (THÔNG TIN)
        updateExpAndInfoIcons();

        // Slot 53: Chiseled Stone Bricks
        ItemStack bricks = new ItemStack(Items.CHISELED_STONE_BRICKS);
        bricks.set(DataComponentTypes.CUSTOM_NAME, noItalic(Text.literal("Tùy chỉnh").formatted(Formatting.AQUA)));
        this.containerInv.setStack(53, bricks);
    }

    private void updateExpAndInfoIcons() {
        // Slot 49: NHẬN EXP (00:16 - 00:17 trong video)
        ItemStack expBottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
        expBottle.set(DataComponentTypes.CUSTOM_NAME, noItalic(Text.literal("NHẬN EXP").formatted(Formatting.GREEN)));
        List<Text> expLore = List.of(
                noItalic(Text.literal("Bấm vào để nhận toàn bộ EXP").formatted(Formatting.WHITE)),
                noItalic(Text.empty()
                        .append(Text.literal("ĐANG CÓ: ").formatted(Formatting.WHITE))
                        .append(Text.literal(String.valueOf(spawnerData.currentExp)).formatted(Formatting.GREEN))
                        .append(Text.literal("/").formatted(Formatting.GRAY))
                        .append(Text.literal(spawnerData.maxExpK + "K").formatted(Formatting.YELLOW)))
        );
        expBottle.set(DataComponentTypes.LORE, new LoreComponent(expLore));
        this.containerInv.setStack(49, expBottle);

        // Slot 50: THÔNG TIN (00:14 trong video)
        ItemStack infoSpawner = new ItemStack(Items.SPAWNER);
        infoSpawner.set(DataComponentTypes.CUSTOM_NAME, noItalic(Text.literal("THÔNG TIN").formatted(Formatting.AQUA)));
        List<Text> infoLore = List.of(
                noItalic(Text.empty()
                        .append(Text.literal("TỐC ĐỘ SPAWN: ").formatted(Formatting.WHITE))
                        .append(Text.literal("5 - 15 giây").formatted(Formatting.GREEN))),
                noItalic(Text.empty()
                        .append(Text.literal("ITEM RƠI: ").formatted(Formatting.WHITE))
                        .append(Text.literal(spawnerData.getMinDrop() + " - " + spawnerData.getMaxDrop()).formatted(Formatting.GREEN))
                        .append(Text.literal(" MỖI LẦN").formatted(Formatting.WHITE))),
                noItalic(Text.empty()
                        .append(Text.literal("SỐ EXP RƠI: ").formatted(Formatting.WHITE))
                        .append(Text.literal(spawnerData.getMinExp() + " - " + spawnerData.getMaxExp()).formatted(Formatting.GREEN))
                        .append(Text.literal(" MỖI LẦN").formatted(Formatting.WHITE))),
                noItalic(Text.empty()
                        .append(Text.literal("ĐANG CÓ: ").formatted(Formatting.WHITE))
                        .append(Text.literal(String.valueOf(spawnerData.currentExp)).formatted(Formatting.GREEN))
                        .append(Text.literal("/").formatted(Formatting.GRAY))
                        .append(Text.literal(spawnerData.maxExpK + "K").formatted(Formatting.YELLOW))),
                Text.empty(),
                noItalic(Text.literal("Interact with Spawn Egg:").formatted(Formatting.GRAY)),
                noItalic(Text.literal("Sets Mob Type").formatted(Formatting.GOLD))
        );
        infoSpawner.set(DataComponentTypes.LORE, new LoreComponent(infoLore));
        this.containerInv.setStack(50, infoSpawner);
    }

    private static Text noItalic(Text text) {
        return text.copy().setStyle(text.getStyle().withItalic(false));
    }

    @Override
    protected void onMouseClick( net.minecraft.screen.slot.Slot slot, int slotId, int button, SlotActionType actionType) {
        if (slot != null && slot.inventory == this.containerInv) {
            int idx = slot.getIndex();
            // Khi nguoi choi bam vao Chai Kinh Nghiem (Slot 49 - NHẬN EXP) nhu o giay 00:16
            if (idx == 49) {
                if (this.spawnerData.currentExp > 0) {
                    int claimed = this.spawnerData.currentExp;
                    this.spawnerData.currentExp = 0;
                    updateExpAndInfoIcons();
                    if (MinecraftClient.getInstance().player != null) {
                        FakeSpawnerManager.triggerExpClaimHud(MinecraftClient.getInstance().player, claimed);
                    }
                }
                return;
            }
            // Khong cho lay cac icon dieu khien o hang cuoi (45..53)
            if (idx >= 45) {
                return;
            }
        }
        super.onMouseClick(slot, slotId, button, actionType);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(
                net.minecraft.client.render.RenderLayer::getGuiTextured,
                TEXTURE,
                i, j,
                0.0F, 0.0F,
                this.backgroundWidth, 6 * 18 + 17,
                256, 256
        );
        context.drawTexture(
                net.minecraft.client.render.RenderLayer::getGuiTextured,
                TEXTURE,
                i, j + 6 * 18 + 17,
                0.0F, 126.0F,
                this.backgroundWidth, 96,
                256, 256
        );
    }
}
