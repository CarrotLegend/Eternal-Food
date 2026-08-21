package com.carrot123.eternal_food.util;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OreRecognitionHelperTest {
    @Test
    void parsesFullRegistryIdsWithoutRequiringInstalledMods() {
        Set<ResourceLocation> parsed = OreRecognitionHelper.parseExtraOreIds(List.of(
                "minecraft:stone",
                "some_uninstalled_mod:ruby_ore",
                "minecraft:stone"
        ));

        assertEquals(Set.of(
                id("minecraft:stone"),
                id("some_uninstalled_mod:ruby_ore")
        ), parsed);
    }

    @Test
    void ignoresMalformedAndNamespaceLessIds() {
        Set<ResourceLocation> parsed = OreRecognitionHelper.parseExtraOreIds(List.of(
                "bad id",
                "missingcolon",
                ":wrong",
                "modid:",
                "too:many:colons",
                "valid_mod:valid_block"
        ));

        assertEquals(Set.of(id("valid_mod:valid_block")), parsed);
    }

    @Test
    void returnsAnImmutableCache() {
        Set<ResourceLocation> parsed = OreRecognitionHelper.parseExtraOreIds(
                List.of("minecraft:stone")
        );

        assertFalse(parsed.isEmpty());
        assertThrows(
                UnsupportedOperationException.class,
                () -> parsed.add(id("minecraft:diamond_ore"))
        );
    }

    private static ResourceLocation id(String value) {
        return Objects.requireNonNull(ResourceLocation.tryParse(value));
    }
}
