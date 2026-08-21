package com.carrot123.eternal_food.client.mineral;

import com.carrot123.eternal_food.EternalFood;
import com.carrot123.eternal_food.registry.ModEffects;
import com.carrot123.eternal_food.util.OreRecognitionHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(
        modid = EternalFood.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class MineralScentClientHandler {
    private static final OreHighlightCache CACHE = new OreHighlightCache();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CACHE.tick(Minecraft.getInstance());
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null
                && minecraft.player.hasEffect(ModEffects.MINERAL_SCENT.get())) {
            OreHighlightRenderer.render(event, CACHE);
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null
                || minecraft.level == null
                || !minecraft.player.hasEffect(ModEffects.MINERAL_SCENT.get())) {
            return;
        }

        Component name = findTargetedOre(minecraft, event.getPartialTick());
        if (name == null) {
            return;
        }

        PoseStack poseStack = event.getGuiGraphics().pose();
        int centerX = event.getWindow().getGuiScaledWidth() / 2;
        int centerY = event.getWindow().getGuiScaledHeight() / 2;
        poseStack.pushPose();
        poseStack.translate(centerX, centerY - 14, 0.0F);
        poseStack.scale(0.75F, 0.75F, 1.0F);
        event.getGuiGraphics().drawString(
                minecraft.font,
                name,
                -minecraft.font.width(name) / 2,
                0,
                0xE8FFF8,
                true
        );
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        CACHE.clear();
    }

    private static Component findTargetedOre(Minecraft minecraft, float partialTick) {
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null) {
            return null;
        }

        Vec3 start = minecraft.player.getEyePosition(partialTick);
        Vec3 end = start.add(minecraft.player.getViewVector(partialTick)
                .scale(OreHighlightCache.SCAN_RADIUS));
        double nearestDistance = Double.MAX_VALUE;
        Component nearestName = null;

        for (BlockPos pos : CACHE.positions()) {
            if (!level.hasChunkAt(pos)) {
                continue;
            }

            var state = level.getBlockState(pos);
            if (!OreRecognitionHelper.isRecognizedOre(state)) {
                continue;
            }

            VoxelShape shape = state.getShape(level, pos, CollisionContext.empty());
            if (shape.isEmpty()) {
                shape = Shapes.block();
            }
            for (AABB localBox : shape.toAabbs()) {
                Optional<Vec3> intersection = localBox.move(pos).clip(start, end);
                if (intersection.isEmpty()) {
                    continue;
                }

                double distance = start.distanceToSqr(intersection.get());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestName = state.getBlock().getName();
                }
            }
        }
        return nearestName;
    }

    private MineralScentClientHandler() {
    }
}
