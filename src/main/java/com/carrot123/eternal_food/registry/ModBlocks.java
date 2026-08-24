package com.carrot123.eternal_food.registry;

import com.carrot123.eternal_food.EternalFood;
import com.carrot123.eternal_food.block.MineralGelBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, EternalFood.MOD_ID);

    public static final RegistryObject<MineralGelBlock> MINERAL_GEL = BLOCKS.register(
        "mineral_gel",
        () -> new MineralGelBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_LIGHT_GREEN)
                        .replaceable()
                        .noCollission()
                        .strength(0.2F)
                        .sound(SoundType.SLIME_BLOCK)
                        .randomTicks()
                        .lightLevel(state -> 7)
                        .pushReaction(PushReaction.DESTROY)
        )
);

    private ModBlocks() {
    }
}
