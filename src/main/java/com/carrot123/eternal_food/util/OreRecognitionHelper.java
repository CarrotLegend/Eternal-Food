package com.carrot123.eternal_food.util;

import com.carrot123.eternal_food.EternalFood;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public final class OreRecognitionHelper {
    private static final AtomicLong REVISION = new AtomicLong();
    private static volatile Set<ResourceLocation> extraOreIds = Set.of();

    public static boolean isRecognizedOre(BlockState state) {
        if (state.is(Tags.Blocks.ORES)) {
            return true;
        }

        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return id != null && extraOreIds.contains(id);
    }

    public static void reloadExtraOreIds(List<? extends String> configuredIds) {
        extraOreIds = parseExtraOreIds(configuredIds);
        REVISION.incrementAndGet();
    }

    public static long revision() {
        return REVISION.get();
    }

    static Set<ResourceLocation> parseExtraOreIds(Iterable<? extends String> configuredIds) {
        Set<ResourceLocation> parsed = new HashSet<>();
        for (String value : configuredIds) {
            ResourceLocation id = parseFullRegistryId(value);
            if (id == null) {
                EternalFood.LOGGER.warn("Ignoring invalid extra ore block id: {}", value);
                continue;
            }
            parsed.add(id);
        }
        return Set.copyOf(parsed);
    }

    private static ResourceLocation parseFullRegistryId(String value) {
        if (value == null) {
            return null;
        }

        int separator = value.indexOf(':');
        if (separator <= 0
                || separator == value.length() - 1
                || separator != value.lastIndexOf(':')) {
            return null;
        }
        return ResourceLocation.tryParse(value);
    }

    private OreRecognitionHelper() {
    }
}
