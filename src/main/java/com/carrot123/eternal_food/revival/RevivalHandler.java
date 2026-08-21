package com.carrot123.eternal_food.revival;

import com.carrot123.eternal_food.EternalFood;
import com.carrot123.eternal_food.item.PhoenixFeatherItem;
import com.carrot123.eternal_food.network.ModNetworking;
import com.carrot123.eternal_food.network.PhoenixFeatherActivationS2CPacket;
import com.carrot123.eternal_food.registry.ModEffects;
import com.carrot123.eternal_food.registry.ModItems;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(
        modid = EternalFood.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RevivalHandler {
    public static final int PHOENIX_COOLDOWN_TICKS = 2400;

    private static final String PARROT_MARK =
            EternalFood.MOD_ID + ":nirvana_cookie_parrot";
    private static final String REVIVAL_IN_PROGRESS =
            EternalFood.MOD_ID + ":revival_in_progress";

    public static void markNirvanaCookieParrot(Parrot parrot) {
        parrot.getPersistentData().putBoolean(PARROT_MARK, true);
    }

    public static void clearNirvanaCookieParrotMark(Parrot parrot) {
        parrot.getPersistentData().remove(PARROT_MARK);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) {
            return;
        }

        if (entity instanceof Parrot parrot
                && parrot.getPersistentData().getBoolean(PARROT_MARK)) {
            reviveMarkedParrot(event, parrot);
            return;
        }

        if (!(entity instanceof ServerPlayer player)
                || player.getPersistentData().getBoolean(REVIVAL_IN_PROGRESS)) {
            return;
        }

        player.getPersistentData().putBoolean(REVIVAL_IN_PROGRESS, true);
        try {
            if (player.hasEffect(ModEffects.NIRVANA.get())) {
                event.setCanceled(true);
                reviveWithNirvana(player);
                return;
            }

            if (hasEquippedPhoenixFeather(player)
                    && !player.getCooldowns().isOnCooldown(ModItems.PHOENIX_FEATHER.get())) {
                event.setCanceled(true);
                reviveWithPhoenixFeather(player);
            }
        } finally {
            player.getPersistentData().remove(REVIVAL_IN_PROGRESS);
        }
    }

    private static void reviveMarkedParrot(
            LivingDeathEvent event,
            Parrot parrot
    ) {
        event.setCanceled(true);
        clearNirvanaCookieParrotMark(parrot);
        parrot.setHealth(1.0F);
        parrot.level().broadcastEntityEvent(parrot, (byte) 35);
        parrot.spawnAtLocation(new ItemStack(ModItems.PHOENIX_FEATHER.get()));
    }

    private static void reviveWithNirvana(ServerPlayer player) {
        player.setHealth(1.0F);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        player.level().broadcastEntityEvent(player, (byte) 35);
    }

    private static void reviveWithPhoenixFeather(ServerPlayer player) {
        player.removeAllEffects();
        player.setHealth(player.getMaxHealth());
        player.getCooldowns().addCooldown(
                ModItems.PHOENIX_FEATHER.get(), PHOENIX_COOLDOWN_TICKS);

        ServerLevel level = player.serverLevel();
        level.sendParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(),
                player.getY() + player.getBbHeight() * 0.5D,
                player.getZ(),
                30,
                player.getBbWidth() * 0.5D,
                player.getBbHeight() * 0.5D,
                player.getBbWidth() * 0.5D,
                0.2D
        );
        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.TOTEM_USE,
                player.getSoundSource(),
                1.0F,
                1.0F
        );
        ModNetworking.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new PhoenixFeatherActivationS2CPacket()
        );
    }

    private static boolean hasEquippedPhoenixFeather(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.findCurios(PhoenixFeatherItem.CHARM_SLOT)
                        .stream()
                        .anyMatch(result -> result.stack().is(
                                ModItems.PHOENIX_FEATHER.get())))
                .orElse(false);
    }

    private RevivalHandler() {
    }
}
