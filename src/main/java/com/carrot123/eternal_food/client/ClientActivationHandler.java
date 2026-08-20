package com.carrot123.eternal_food.client;

import com.carrot123.eternal_food.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ClientActivationHandler {
    public static void showPhoenixFeather() {
        Minecraft.getInstance().gameRenderer.displayItemActivation(
                new ItemStack(ModItems.PHOENIX_FEATHER.get()));
    }

    private ClientActivationHandler() {
    }
}

