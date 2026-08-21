package com.carrot123.eternal_food.registry;

import com.carrot123.eternal_food.EternalFood;
import com.carrot123.eternal_food.item.NirvanaCookieItem;
import com.carrot123.eternal_food.item.MineralGelBlockItem;
import com.carrot123.eternal_food.item.PhoenixFeatherItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EternalFood.MOD_ID);

    public static final RegistryObject<Item> NIRVANA_COOKIE = ITEMS.register(
            "nirvana_cookie",
            () -> new NirvanaCookieItem(new Item.Properties()
                    .food(Foods.COOKIE)
                    .rarity(Rarity.RARE)
                    .fireResistant())
    );

    public static final RegistryObject<Item> PHOENIX_FEATHER = ITEMS.register(
            "phoenix_feather",
            PhoenixFeatherItem::new
    );

    public static final RegistryObject<Item> MINERAL_GEL = ITEMS.register(
            "mineral_gel",
            () -> new MineralGelBlockItem(
                    ModBlocks.MINERAL_GEL.get(),
                    new Item.Properties().food(new FoodProperties.Builder()
                            .nutrition(1)
                            .saturationMod(1.0F)
                            .build()))
    );

    private ModItems() {
    }
}
