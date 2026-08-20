package com.carrot123.eternal_food.network;

import com.carrot123.eternal_food.client.ClientActivationHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class PhoenixFeatherActivationS2CPacket {
    public static void encode(
            PhoenixFeatherActivationS2CPacket packet,
            FriendlyByteBuf buffer
    ) {
    }

    public static PhoenixFeatherActivationS2CPacket decode(FriendlyByteBuf buffer) {
        return new PhoenixFeatherActivationS2CPacket();
    }

    public static void handle(
            PhoenixFeatherActivationS2CPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> ClientActivationHandler::showPhoenixFeather));
        context.setPacketHandled(true);
    }
}

