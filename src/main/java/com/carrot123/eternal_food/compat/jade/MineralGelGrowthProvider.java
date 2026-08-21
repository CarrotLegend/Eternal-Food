package com.carrot123.eternal_food.compat.jade;

import com.carrot123.eternal_food.EternalFood;
import com.carrot123.eternal_food.block.MineralGelBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum MineralGelGrowthProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID =
            new ResourceLocation(EternalFood.MOD_ID, "mineral_gel_growth");
    private static final String[] GROWTH_PERCENTAGES = {"0%", "33%", "67%"};

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockState state = accessor.getBlockState();
        int age = state.getValue(MineralGelBlock.AGE);
        if (age >= MineralGelBlock.MAX_AGE) {
            tooltip.add(Component.translatable(
                    "tooltip.jade.crop_growth",
                    IThemeHelper.get().success(Component.translatable("tooltip.jade.crop_mature"))
            ));
            return;
        }

        tooltip.add(Component.translatable(
                "tooltip.jade.crop_growth",
                IThemeHelper.get().info(GROWTH_PERCENTAGES[age])
        ));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
