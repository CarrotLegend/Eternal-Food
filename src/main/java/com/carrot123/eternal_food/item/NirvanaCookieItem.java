package com.carrot123.eternal_food.item;

import com.carrot123.eternal_food.registry.ModEffects;
import com.carrot123.eternal_food.revival.RevivalHandler;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class NirvanaCookieItem extends Item {
    public static final int NIRVANA_DURATION_TICKS = 6000;

    public NirvanaCookieItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide && livingEntity instanceof Player player) {
            player.removeEffect(ModEffects.NIRVANA.get());
            player.addEffect(new MobEffectInstance(
                    ModEffects.NIRVANA.get(), NIRVANA_DURATION_TICKS, 0));
        }
        return result;
    }

    @Override
    public InteractionResult interactLivingEntity(
            ItemStack stack,
            Player player,
            LivingEntity target,
            InteractionHand hand
    ) {
        if (!(target instanceof Parrot parrot)) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        RevivalHandler.markNirvanaCookieParrot(parrot);
        boolean hurt = parrot.hurt(
                parrot.damageSources().playerAttack(player), Float.MAX_VALUE);
        if (!hurt) {
            RevivalHandler.clearNirvanaCookieParrotMark(parrot);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean canBeHurtBy(net.minecraft.world.damagesource.DamageSource source) {
        return !source.is(DamageTypeTags.IS_EXPLOSION) && super.canBeHurtBy(source);
    }
}

