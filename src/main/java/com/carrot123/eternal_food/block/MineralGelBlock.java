package com.carrot123.eternal_food.block;

import com.carrot123.eternal_food.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;

import java.util.List;

public final class MineralGelBlock extends MultifaceBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    public static final int MAX_AGE = 3;
    public static final int MATURE_AGE = 2;
    public static final int NATURAL_GROWTH_CHANCE = 3;

    private final MultifaceSpreader spreader;

    public MineralGelBlock(Properties properties) {
        super(properties);
        this.spreader = new MultifaceSpreader(this);
        registerDefaultState(defaultBlockState().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return spreader;
    }

    @Override
    public boolean isValidStateForPlacement(
            BlockGetter level,
            BlockState state,
            BlockPos pos,
            Direction direction
    ) {
        BlockState support = level.getBlockState(pos.relative(direction));
        return isAllowedSupport(support)
                && super.isValidStateForPlacement(level, state, pos, direction);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction direction : availableFaces(state)) {
            if (!canRemainAttached(level, pos, direction)) {
                return false;
            }
        }
        return hasAnyFace(state);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        BlockState updated = super.updateShape(
                state, direction, neighborState, level, pos, neighborPos);
        if (updated.isAir()) {
            return updated;
        }

        if (hasFace(updated, direction)
                && !canRemainAttached(level, pos, direction)) {
            updated = updated.setValue(getFaceProperty(direction), false);
        }
        return hasAnyFace(updated) ? updated : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE && random.nextInt(NATURAL_GROWTH_CHANCE) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (player.getItemInHand(hand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }

        int age = state.getValue(AGE);
        if (age < MATURE_AGE) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            int amount = age == MATURE_AGE ? 1 : 2 + level.random.nextInt(2);
            popResource(level, pos, new ItemStack(ModItems.MINERAL_GEL.get(), amount));
            level.setBlock(pos, state.setValue(AGE, 0), Block.UPDATE_ALL);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isValidBonemealTarget(
            LevelReader level,
            BlockPos pos,
            BlockState state,
            boolean isClient
    ) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(
            Level level,
            RandomSource random,
            BlockPos pos,
            BlockState state
    ) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void performBonemeal(
            ServerLevel level,
            RandomSource random,
            BlockPos pos,
            BlockState state
    ) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE) {
            level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_ALL);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        int faceCount = availableFaces(state).size();
        return faceCount == 0
                ? List.of()
                : List.of(new ItemStack(ModItems.MINERAL_GEL.get(), faceCount));
    }

    private static boolean isAllowedSupport(BlockState state) {
        return state.is(Tags.Blocks.ORES) || state.is(BlockTags.BASE_STONE_OVERWORLD);
    }

    private static boolean canRemainAttached(
            BlockGetter level,
            BlockPos pos,
            Direction direction
    ) {
        BlockPos supportPos = pos.relative(direction);
        BlockState support = level.getBlockState(supportPos);
        return isAllowedSupport(support)
                && canAttachTo(level, direction, supportPos, support);
    }
}
