package com.carrot123.eternal_food.network;

import com.carrot123.eternal_food.EternalFood;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EternalFood.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static boolean registered;

    public static synchronized void register() {
        if (registered) {
            return;
        }
        registered = true;
        CHANNEL.registerMessage(
                0,
                PhoenixFeatherActivationS2CPacket.class,
                PhoenixFeatherActivationS2CPacket::encode,
                PhoenixFeatherActivationS2CPacket::decode,
                PhoenixFeatherActivationS2CPacket::handle
        );
    }

    private ModNetworking() {
    }
}

