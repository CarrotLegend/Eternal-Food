package com.carrot123.eternal_food.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class NirvanaEffect extends MobEffect {
    private static final int TOTEM_GOLD = 0xE8C34A;

    public NirvanaEffect() {
        super(MobEffectCategory.BENEFICIAL, TOTEM_GOLD);
    }
}

