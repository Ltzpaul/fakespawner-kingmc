package vn.kingsmp.fakespawner.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Unique
    private SkeletonEntity fakespawner$cachedSkeleton;

    @Inject(method = "renderEntities", at = @At("RETURN"))
    private void fakespawner$renderSpinningSkeletonInCopperBlocks(
            MatrixStack matrices,
            VertexConsumerProvider.Immediate immediate,
            Camera camera,
            RenderTickCounter tickCounter,
            java.util.List<?> entities,
            CallbackInfo ci
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        if (this.fakespawner$cachedSkeleton == null || this.fakespawner$cachedSkeleton.getWorld() != client.world) {
            this.fakespawner$cachedSkeleton = new SkeletonEntity(EntityType.SKELETON, client.world);
        }

        Vec3d camPos = camera.getPos();
        BlockPos center = client.player.getBlockPos();
        float tickDelta = tickCounter.getTickDelta(false);
        float rotationAngle = ((client.world.getTime() + tickDelta) * 10.0F) % 360.0F;
        EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();

        for (BlockPos pos : BlockPos.iterate(center.add(-12, -6, -12), center.add(12, 6, 12))) {
            if (client.world.getBlockState(pos).isOf(Blocks.COPPER_BLOCK)) {
                matrices.push();
                matrices.translate(
                        pos.getX() + 0.5 - camPos.x,
                        pos.getY() + 0.4 - camPos.y,
                        pos.getZ() + 0.5 - camPos.z
                );
                matrices.scale(0.4375F, 0.4375F, 0.4375F);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationAngle));
                int light = dispatcher.getLight(this.fakespawner$cachedSkeleton, tickDelta);
                dispatcher.render(
                        this.fakespawner$cachedSkeleton,
                        0.0, 0.0, 0.0,
                        tickDelta,
                        matrices,
                        immediate,
                        light
                );
                matrices.pop();
            }
        }
    }
}
