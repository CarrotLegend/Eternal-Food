package com.carrot123.eternal_food;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

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
        assertTrue(modsToml.contains("modId=\"jade\"\nmandatory=false\nversionRange=\"[11,12)\"\nordering=\"AFTER\"\nside=\"CLIENT\""));
    }

    @Test
    void creativeTabUsesTheCookieAndContainsEachObtainableItemOnce() throws IOException {
        JsonObject zh = readJson("assets/eternal_food/lang/zh_cn.json");
        JsonObject en = readJson("assets/eternal_food/lang/en_us.json");
        String main = readJava("EternalFood.java");
        String tabs = readJava("registry/ModCreativeTabs.java");

        assertEquals("永恒食物", zh.get("creativetab.eternal_food").getAsString());
        assertEquals("Eternal Food", en.get("creativetab.eternal_food").getAsString());
        assertEquals("矿物凝胶生长进度",
                zh.get("config.jade.plugin_eternal_food.mineral_gel_growth").getAsString());
        assertEquals("Mineral Gel Growth",
                en.get("config.jade.plugin_eternal_food.mineral_gel_growth").getAsString());
        assertTrue(main.contains("ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus)"));
        assertTrue(tabs.contains("DeferredRegister.create(Registries.CREATIVE_MODE_TAB"));
        assertTrue(tabs.contains("\"eternal_food\""));
        assertTrue(tabs.contains("new ItemStack(ModItems.NIRVANA_COOKIE.get())"));
        assertEquals(2, occurrences(tabs, "ModItems.NIRVANA_COOKIE.get()"));
        assertEquals(1, occurrences(tabs, "output.accept(ModItems.NIRVANA_COOKIE.get())"));
        assertEquals(1, occurrences(tabs, "output.accept(ModItems.PHOENIX_FEATHER.get())"));
        assertEquals(1, occurrences(tabs, "output.accept(ModItems.MINERAL_GEL.get())"));
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
    void mineralGelResourcesExposeAllFourMultifaceStages() throws IOException {
        JsonObject zh = readJson("assets/eternal_food/lang/zh_cn.json");
        JsonObject en = readJson("assets/eternal_food/lang/en_us.json");
        assertEquals("矿物凝胶", zh.get("block.eternal_food.mineral_gel").getAsString());
        assertEquals("矿物嗅觉", zh.get("effect.eternal_food.mineral_scent").getAsString());
        assertEquals("Mineral Gel", en.get("block.eternal_food.mineral_gel").getAsString());
        assertEquals("Mineral Scent", en.get("effect.eternal_food.mineral_scent").getAsString());

        JsonObject blockstate = readJson("assets/eternal_food/blockstates/mineral_gel.json");
        assertEquals(24, blockstate.getAsJsonArray("multipart").size());
        Set<String> models = new HashSet<>();
        blockstate.getAsJsonArray("multipart").forEach(part -> {
            JsonObject entry = part.getAsJsonObject();
            JsonObject when = entry.getAsJsonObject("when");
            assertEquals(2, when.size());
            assertTrue(when.has("age"));
            assertEquals(1L, Set.of("north", "east", "south", "west", "up", "down").stream()
                    .filter(when::has)
                    .count());
            models.add(entry.getAsJsonObject("apply").get("model").getAsString());
        });

        JsonObject template = readJson(
                "assets/eternal_food/models/block/template_mineral_gel_face.json");
        assertFalse(template.get("ambientocclusion").getAsBoolean());
        assertEquals(2, template.getAsJsonArray("elements").size());
        assertEquals(0.10D, template.getAsJsonArray("elements").get(0).getAsJsonObject()
                .getAsJsonArray("from").get(2).getAsDouble());
        assertEquals(0.20D, template.getAsJsonArray("elements").get(1).getAsJsonObject()
                .getAsJsonArray("from").get(2).getAsDouble());
        assertEquals("#base", template.getAsJsonArray("elements").get(0).getAsJsonObject()
                .getAsJsonObject("faces").getAsJsonObject("north").get("texture").getAsString());
        assertEquals("#overlay", template.getAsJsonArray("elements").get(1).getAsJsonObject()
                .getAsJsonObject("faces").getAsJsonObject("north").get("texture").getAsString());

        for (int age = 0; age <= 3; age++) {
            String modelId = "eternal_food:block/mineral_gel_stage" + age;
            assertTrue(models.contains(modelId));
            JsonObject model = readJson(
                    "assets/eternal_food/models/block/mineral_gel_stage" + age + ".json");
            assertEquals("eternal_food:block/template_mineral_gel_face",
                    model.get("parent").getAsString());
            JsonObject textures = model.getAsJsonObject("textures");
            assertEquals("eternal_food:block/mineral_gel_stage_" + age,
                    textures.get("particle").getAsString());
            assertEquals("eternal_food:block/mineral_gel_stage_" + age,
                    textures.get("base").getAsString());
            assertEquals("eternal_food:block/mineral_gel_stage_" + age + "_glow",
                    textures.get("overlay").getAsString());
            assertTrue(Files.exists(RESOURCES.resolve(
                    "assets/eternal_food/textures/block/mineral_gel_stage_" + age + ".png")));
            assertTrue(Files.exists(RESOURCES.resolve(
                    "assets/eternal_food/textures/block/mineral_gel_stage_" + age + "_glow.png")));
        }

        assertEquals("eternal_food:item/mineral_gel",
                readJson("assets/eternal_food/models/item/mineral_gel.json")
                        .getAsJsonObject("textures").get("layer0").getAsString());
        assertTrue(Files.exists(RESOURCES.resolve(
                "assets/eternal_food/textures/mob_effect/mineral_scent.png")));

        for (String path : Set.of(
                "assets/eternal_food/blockstates/mineral_gel.json",
                "assets/eternal_food/models/block/template_mineral_gel_face.json",
                "assets/eternal_food/models/block/mineral_gel_stage0.json",
                "assets/eternal_food/models/block/mineral_gel_stage1.json",
                "assets/eternal_food/models/block/mineral_gel_stage2.json",
                "assets/eternal_food/models/block/mineral_gel_stage3.json",
                "assets/eternal_food/models/item/mineral_gel.json")) {
            assertFalse(Files.readString(RESOURCES.resolve(path)).contains("glow_lichen"),
                    () -> "Vanilla glow lichen leaked into " + path);
        }
        assertFalse(Files.exists(Path.of("src", "generated", "resources", "assets",
                "eternal_food", "blockstates", "mineral_gel.json")));
    }

    @Test
    void jadeCompatibilityIsOptionalClientOnlyAndUsesLocalBlockState() throws IOException {
        String build = Files.readString(Path.of("build.gradle"));
        String plugin = readJava("compat/jade/EternalFoodJadePlugin.java");
        String provider = readJava("compat/jade/MineralGelGrowthProvider.java");

        assertTrue(build.contains("compileOnly fg.deobf('curse.maven:jade-324717:6855440')"));
        assertTrue(build.contains("gradleProperty('includeJade')"));
        assertTrue(build.contains("runtimeOnly fg.deobf('curse.maven:jade-324717:6855440')"));
        assertFalse(build.contains("jarJar"));
        assertTrue(plugin.contains("@WailaPlugin"));
        assertTrue(plugin.contains("implements IWailaPlugin"));
        assertTrue(plugin.contains("registerBlockComponent"));
        assertTrue(plugin.contains("MineralGelBlock.class"));
        assertTrue(provider.contains("accessor.getBlockState()"));
        assertTrue(provider.contains("MineralGelBlock.AGE"));
        assertTrue(provider.contains("\"mineral_gel_growth\""));
        assertTrue(provider.contains("{\"0%\", \"33%\", \"67%\"}"));
        assertTrue(provider.contains("\"tooltip.jade.crop_growth\""));
        assertTrue(provider.contains("\"tooltip.jade.crop_mature\""));
        assertTrue(provider.contains("IThemeHelper.get().info"));
        assertTrue(provider.contains("IThemeHelper.get().success"));
        assertFalse(provider.contains("ServerData"));
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

    @Test
    void mineralGelFoodGrowthAndHarvestContractsRemainPinned() throws IOException {
        String main = readJava("EternalFood.java");
        String blocks = readJava("registry/ModBlocks.java");
        String items = readJava("registry/ModItems.java");
        String effects = readJava("registry/ModEffects.java");
        String gel = readJava("block/MineralGelBlock.java");
        String gelItem = readJava("item/MineralGelBlockItem.java");

        assertTrue(main.contains("ModBlocks.BLOCKS.register(modEventBus)"));
        assertTrue(blocks.contains("\"mineral_gel\""));
        assertTrue(items.contains("new MineralGelBlockItem"));
        assertTrue(effects.contains("\"mineral_scent\""));
        assertTrue(items.contains(".nutrition(1)"));
        assertTrue(items.contains(".saturationMod(1.0F)"));
        assertTrue(gelItem.contains("USE_DURATION_TICKS = 16"));
        assertTrue(gelItem.contains("MINERAL_SCENT_DURATION_TICKS = 2400"));

        assertTrue(gel.contains("IntegerProperty.create(\"age\", 0, 3)"));
        assertTrue(gel.contains("NATURAL_GROWTH_CHANCE = 3"));
        assertTrue(gel.contains("state.is(Tags.Blocks.ORES)"));
        assertTrue(gel.contains("state.is(BlockTags.BASE_STONE_OVERWORLD)"));
        assertTrue(gel.contains("canRemainAttached(level, pos, direction)"));
        assertTrue(gel.contains("player.getItemInHand(hand).is(Items.BONE_MEAL)"));
        assertTrue(gel.contains("age == MATURE_AGE ? 1 : 2 + level.random.nextInt(2)"));
        assertTrue(gel.contains("state.setValue(AGE, 0)"));
        assertTrue(gel.contains("state.setValue(AGE, age + 1)"));
        assertTrue(gel.contains("availableFaces(state).size()"));
    }

    @Test
    void mineralScentClientContractsRemainPinned() throws IOException {
        String main = readJava("EternalFood.java");
        String config = readJava("config/EternalFoodClientConfig.java");
        String helper = readJava("util/OreRecognitionHelper.java");
        String cache = readJava("client/mineral/OreHighlightCache.java");
        String handler = readJava("client/mineral/MineralScentClientHandler.java");
        String renderer = readJava("client/mineral/OreHighlightRenderer.java");
        String renderType = readJava("client/mineral/OreHighlightRenderType.java");

        assertTrue(cache.contains("SCAN_RADIUS = 16"));
        assertTrue(cache.contains("SCAN_INTERVAL_TICKS = 10"));
        assertTrue(cache.contains("level.hasChunkAt(pos)"));
        assertTrue(main.contains("EternalFoodClientConfig.register(context)"));
        assertTrue(config.contains("ModConfig.Type.CLIENT"));
        assertTrue(config.contains("\"extra_ore_blocks\""));
        assertTrue(config.contains("List.of()"));
        assertTrue(config.contains("-client.toml"));
        assertTrue(helper.contains("state.is(Tags.Blocks.ORES)"));
        assertTrue(helper.contains("Set<ResourceLocation>"));
        assertTrue(helper.contains("ForgeRegistries.BLOCKS.getKey(state.getBlock())"));
        assertTrue(helper.contains("ResourceLocation.tryParse(value)"));
        assertTrue(helper.contains("REVISION.incrementAndGet()"));
        assertTrue(cache.contains("OreRecognitionHelper.isRecognizedOre"));
        assertTrue(cache.contains("OreRecognitionHelper.revision()"));
        assertTrue(handler.contains("OreRecognitionHelper.isRecognizedOre"));
        assertTrue(renderer.contains("OreRecognitionHelper.isRecognizedOre"));
        assertFalse(cache.contains("Tags.Blocks.ORES"));
        assertFalse(handler.contains("Tags.Blocks.ORES"));
        assertFalse(renderer.contains("Tags.Blocks.ORES"));
        assertTrue(handler.contains("Stage.AFTER_TRANSLUCENT_BLOCKS"));
        assertTrue(handler.contains("VanillaGuiOverlay.CROSSHAIR"));
        assertTrue(handler.contains("localBox.move(pos).clip(start, end)"));
        assertTrue(renderer.contains("getShape(level, pos, CollisionContext.empty())"));
        assertTrue(renderType.contains("NO_DEPTH_TEST"));
        assertTrue(renderType.contains("COLOR_WRITE"));

        String clientSetup = readJava("client/ClientModEvents.java");
        assertTrue(clientSetup.contains("Dist.CLIENT"));
        assertTrue(clientSetup.contains("RenderType.cutout()"));
        assertTrue(clientSetup.contains("ModBlocks.MINERAL_GEL.get()"));
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
