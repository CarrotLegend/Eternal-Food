package com.carrot123.eternal_food;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EternalFoodResourceTest {
    private static final Path RESOURCES = Path.of("src", "main", "resources");

    @Test
    void declaresOnlyStandaloneMandatoryDependencies() throws IOException {
        String modsToml = Files.readString(RESOURCES.resolve("META-INF/mods.toml"));
        String removedCoreId = "until" + "_eternity";
        assertFalse(modsToml.contains("modId=\"" + removedCoreId + "\""));
        assertTrue(modsToml.contains("modId=\"curios\""));
        assertTrue(modsToml.contains("modId=\"forge\""));
        assertTrue(modsToml.contains("modId=\"minecraft\""));
        assertTrue(modsToml.contains("side=\"BOTH\""));
    }

    @Test
    void phoenixFeatherUsesAndAssignsTheStandardCharmSlot() throws IOException {
        JsonObject tag = JsonParser.parseString(Files.readString(RESOURCES.resolve(
                "data/curios/tags/items/charm.json"))).getAsJsonObject();
        assertEquals("eternal_food:phoenix_feather",
                tag.getAsJsonArray("values").get(0).getAsString());
        assertFalse(Files.exists(RESOURCES.resolve(
                "data/eternal_food/curios/slots/charm.json")));

        JsonObject assignment = readJson(
                "data/eternal_food/curios/entities/player.json");
        assertEquals("minecraft:player",
                assignment.getAsJsonArray("entities").get(0).getAsString());
        assertEquals("charm",
                assignment.getAsJsonArray("slots").get(0).getAsString());
    }

    @Test
    void languageAndModelsContainRequiredEntries() throws IOException {
        JsonObject zh = readJson("assets/eternal_food/lang/zh_cn.json");
        JsonObject en = readJson("assets/eternal_food/lang/en_us.json");
        assertEquals("凤凰翎", zh.get("item.eternal_food.phoenix_feather").getAsString());
        assertEquals("效果有2分钟冷却。",
                zh.get("tooltip.eternal_food.phoenix_feather.cooldown").getAsString());
        assertEquals("Phoenix Feather",
                en.get("item.eternal_food.phoenix_feather").getAsString());
        assertEquals("eternal_food:item/nirvana_cookie",
                readJson("assets/eternal_food/models/item/nirvana_cookie.json")
                        .getAsJsonObject("textures").get("layer0").getAsString());
        assertEquals("eternal_food:item/phoenix_feather",
                readJson("assets/eternal_food/models/item/phoenix_feather.json")
                        .getAsJsonObject("textures").get("layer0").getAsString());
    }

    @Test
    void commonSourcesDoNotImportMinecraftClientClasses() throws IOException {
        Path javaRoot = Path.of("src", "main", "java");
        try (var files = Files.walk(javaRoot)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                if (file.toString().contains("\\client\\")) {
                    continue;
                }
                assertFalse(Files.readString(file).contains("import net.minecraft.client."),
                        () -> "Client import leaked into common source: " + file);
            }
        }
    }

    @Test
    void itemAndRevivalContractsRemainPinned() throws IOException {
        String registry = readJava("registry/ModItems.java");
        String cookie = readJava("item/NirvanaCookieItem.java");
        String feather = readJava("item/PhoenixFeatherItem.java");
        String revival = readJava("revival/RevivalHandler.java");

        assertTrue(registry.contains(".food(Foods.COOKIE)"));
        assertEquals(2, occurrences(registry + feather, ".rarity(Rarity.RARE)"));
        assertEquals(2, occurrences(registry + feather, ".fireResistant()"));
        assertTrue(cookie.contains("NIRVANA_DURATION_TICKS = 6000"));
        assertTrue(cookie.contains("DamageTypeTags.IS_EXPLOSION"));
        assertTrue(feather.contains("DamageTypeTags.IS_EXPLOSION"));
        assertTrue(feather.contains("MAX_HEALTH_BONUS = 0.20D"));
        assertTrue(feather.contains("AttributeModifier.Operation.MULTIPLY_TOTAL"));
        assertTrue(revival.contains("PHOENIX_COOLDOWN_TICKS = 2400"));
    }

    private static JsonObject readJson(String path) throws IOException {
        return JsonParser.parseString(Files.readString(RESOURCES.resolve(path))).getAsJsonObject();
    }

    private static String readJava(String path) throws IOException {
        return Files.readString(Path.of(
                "src", "main", "java", "com", "carrot123", "eternal_food", path));
    }

    private static int occurrences(String text, String needle) {
        return text.split(java.util.regex.Pattern.quote(needle), -1).length - 1;
    }
}
