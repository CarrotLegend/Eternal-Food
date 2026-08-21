package com.carrot123.eternal_food.client.mineral;

import com.carrot123.eternal_food.registry.ModEffects;
import com.carrot123.eternal_food.util.OreRecognitionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OreHighlightCache {
    public static final int SCAN_RADIUS = 16;
    public static final int SCAN_INTERVAL_TICKS = 10;

    private List<BlockPos> orePositions = List.of();
    private ResourceKey<Level> dimension;
    private int ticksUntilScan;
    private long recognitionRevision = OreRecognitionHelper.revision();

    public void tick(Minecraft minecraft) {
        long currentRevision = OreRecognitionHelper.revision();
        if (currentRevision != recognitionRevision) {
            clear();
            recognitionRevision = currentRevision;
        }

        if (minecraft.level == null
                || minecraft.player == null
                || !minecraft.player.isAlive()
                || !minecraft.player.hasEffect(ModEffects.MINERAL_SCENT.get())) {
            clear();
            return;
        }

        ResourceKey<Level> currentDimension = minecraft.level.dimension();
        if (!currentDimension.equals(dimension)) {
            clear();
            dimension = currentDimension;
        }

        if (ticksUntilScan > 0) {
            ticksUntilScan--;
            return;
        }

        scan(minecraft.level, minecraft.player.blockPosition());
        ticksUntilScan = SCAN_INTERVAL_TICKS - 1;
    }

    public List<BlockPos> positions() {
        return orePositions;
    }

    public void clear() {
        orePositions = List.of();
        dimension = null;
        ticksUntilScan = 0;
    }

    private void scan(ClientLevel level, BlockPos center) {
        List<BlockPos> found = new ArrayList<>();
        int radiusSquared = SCAN_RADIUS * SCAN_RADIUS;
        int minY = Math.max(level.getMinBuildHeight(), center.getY() - SCAN_RADIUS);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, center.getY() + SCAN_RADIUS);

        for (int x = center.getX() - SCAN_RADIUS; x <= center.getX() + SCAN_RADIUS; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = center.getZ() - SCAN_RADIUS; z <= center.getZ() + SCAN_RADIUS; z++) {
                    int dx = x - center.getX();
                    int dy = y - center.getY();
                    int dz = z - center.getZ();
                    if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(x, y, z);
                    if (level.hasChunkAt(pos)
                            && OreRecognitionHelper.isRecognizedOre(level.getBlockState(pos))) {
                        found.add(pos);
                    }
                }
            }
        }
        orePositions = Collections.unmodifiableList(found);
    }
}
