package com.carrot123.eternal_food.registry;

import com.carrot123.eternal_food.EternalFood;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EternalFood.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ETERNAL_FOOD = CREATIVE_MODE_TABS.register(
            "eternal_food",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.eternal_food"))
                    .icon(() -> new ItemStack(ModItems.NIRVANA_COOKIE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.NIRVANA_COOKIE.get());
                        output.accept(ModItems.PHOENIX_FEATHER.get());
                        output.accept(ModItems.MINERAL_GEL.get());
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }
}
