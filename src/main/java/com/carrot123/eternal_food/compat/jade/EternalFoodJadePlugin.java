package com.carrot123.eternal_food.compat.jade;

import com.carrot123.eternal_food.block.MineralGelBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class EternalFoodJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(
                MineralGelGrowthProvider.INSTANCE,
                MineralGelBlock.class
        );
    }
}
