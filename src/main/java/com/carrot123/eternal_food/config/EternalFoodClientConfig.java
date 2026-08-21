package com.carrot123.eternal_food.config;

import com.carrot123.eternal_food.EternalFood;
import com.carrot123.eternal_food.util.OreRecognitionHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

public final class EternalFoodClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXTRA_ORE_BLOCKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        EXTRA_ORE_BLOCKS = builder
                .comment(
                        "Additional block registry IDs that Mineral Scent should recognize as ores.",
                        "Use this for modded ores that are not included in the Forge ore tag.",
                        "Use full registry IDs in the form \"modid:block_name\".",
                        "Example: \"examplemod:ruby_ore\""
                )
                .defineListAllowEmpty(
                        "extra_ore_blocks",
                        List.of(),
                        entry -> entry instanceof String
                );
        SPEC = builder.build();
    }

    public static void register(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(EternalFoodClientConfig::onConfigLoading);
        modEventBus.addListener(EternalFoodClientConfig::onConfigReloading);
        context.registerConfig(
                ModConfig.Type.CLIENT,
                SPEC,
                EternalFood.MOD_ID + "-client.toml"
        );
    }

    private static void onConfigLoading(ModConfigEvent.Loading event) {
        rebuildOreRecognitionCache(event);
    }

    private static void onConfigReloading(ModConfigEvent.Reloading event) {
        rebuildOreRecognitionCache(event);
    }

    private static void rebuildOreRecognitionCache(ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            OreRecognitionHelper.reloadExtraOreIds(EXTRA_ORE_BLOCKS.get());
        }
    }

    private EternalFoodClientConfig() {
    }
}
