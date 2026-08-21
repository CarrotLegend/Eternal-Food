package com.carrot123.eternal_food.client.mineral;

import com.carrot123.eternal_food.util.OreRecognitionHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderLevelStageEvent;

public final class OreHighlightRenderer {
    private static final float RED = 0.55F;
    private static final float GREEN = 1.0F;
    private static final float BLUE = 0.92F;
    private static final float ALPHA = 1.0F;

    public static void render(
            RenderLevelStageEvent event,
            OreHighlightCache cache
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null || cache.positions().isEmpty()) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();

        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        try {
            VertexConsumer consumer = buffers.getBuffer(OreHighlightRenderType.ORE_XRAY_LINES);
            int radiusSquared = OreHighlightCache.SCAN_RADIUS * OreHighlightCache.SCAN_RADIUS;
            BlockPos playerPos = minecraft.player.blockPosition();

            for (BlockPos pos : cache.positions()) {
                if (!level.hasChunkAt(pos)
                        || pos.distSqr(playerPos) > radiusSquared
                        || !OreRecognitionHelper.isRecognizedOre(level.getBlockState(pos))) {
                    continue;
                }

                VoxelShape shape = level.getBlockState(pos)
                        .getShape(level, pos, CollisionContext.empty());
                if (shape.isEmpty()) {
                    shape = Shapes.block();
                }
                for (AABB localBox : shape.toAabbs()) {
                    LevelRenderer.renderLineBox(
                            poseStack,
                            consumer,
                            localBox.move(pos),
                            RED,
                            GREEN,
                            BLUE,
                            ALPHA
                    );
                }
            }
            buffers.endBatch(OreHighlightRenderType.ORE_XRAY_LINES);
        } finally {
            poseStack.popPose();
        }
    }

    private OreHighlightRenderer() {
    }
}
