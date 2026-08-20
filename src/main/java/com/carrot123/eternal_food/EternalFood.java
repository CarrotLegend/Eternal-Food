package com.carrot123.eternal_food;

import com.carrot123.eternal_food.network.ModNetworking;
import com.carrot123.eternal_food.registry.ModEffects;
import com.carrot123.eternal_food.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EternalFood.MOD_ID)
public final class EternalFood {
    public static final String MOD_ID = "eternal_food";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EternalFood(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::register);
    }
}

