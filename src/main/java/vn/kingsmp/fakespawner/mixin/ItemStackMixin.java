package vn.kingsmp.fakespawner.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vn.kingsmp.fakespawner.FakeSpawnerManager;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void fakespawner$overrideCopperBlockName(CallbackInfoReturnable<Text> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.isOf(Items.COPPER_BLOCK)) {
            cir.setReturnValue(FakeSpawnerManager.getFakeSpawnerName());
        }
    }
}
