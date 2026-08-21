package com.carrot123.eternal_food.item;

import com.carrot123.eternal_food.registry.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public final class MineralGelBlockItem extends BlockItem {
    public static final int USE_DURATION_TICKS = 16;
    public static final int MINERAL_SCENT_DURATION_TICKS = 2400;

    public MineralGelBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION_TICKS;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide) {
            entity.removeEffect(ModEffects.MINERAL_SCENT.get());
            entity.addEffect(new MobEffectInstance(
                    ModEffects.MINERAL_SCENT.get(), MINERAL_SCENT_DURATION_TICKS, 0));
        }
        return result;
    }
}
