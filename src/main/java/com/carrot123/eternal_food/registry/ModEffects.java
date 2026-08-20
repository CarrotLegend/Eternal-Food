package com.carrot123.eternal_food.registry;

import com.carrot123.eternal_food.EternalFood;
import com.carrot123.eternal_food.effect.NirvanaEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, EternalFood.MOD_ID);

    public static final RegistryObject<MobEffect> NIRVANA =
            EFFECTS.register("nirvana", NirvanaEffect::new);

    private ModEffects() {
    }
}

