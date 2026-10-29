package vn.kingsmp.fakespawner.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow
    public abstract Block getBlock();

    @Inject(method = "isOpaque", at = @At("HEAD"), cancellable = true)
    private void fakespawner$makeCopperSpawnerNonOpaque(CallbackInfoReturnable<Boolean> cir) {
        if (this.getBlock() == Blocks.COPPER_BLOCK) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getAmbientOcclusionLightLevel", at = @At("HEAD"), cancellable = true)
    private void fakespawner$brightSpawnerInterior(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (this.getBlock() == Blocks.COPPER_BLOCK) {
            cir.setReturnValue(1.0F);
        }
    }
}
