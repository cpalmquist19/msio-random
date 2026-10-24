package com.minesharesiorandom.block;

import com.minesharesiorandom.block.entity.CragbeastEggBlockEntity;
import com.minesharesiorandom.registry.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public final class CragbeastEggBlock extends Block implements BlockEntityProvider {
    private static final VoxelShape EGG_SHAPE = Block.createCuboidShape(
            2.0, 0.0, 2.0,
            14.0, 15.0, 14.0
    );

    public CragbeastEggBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CragbeastEggBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return EGG_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return EGG_SHAPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            World world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (type != ModBlockEntities.CRAGBEAST_EGG) {
            return null;
        }

        return (BlockEntityTicker<T>) (BlockEntityTicker<CragbeastEggBlockEntity>) CragbeastEggBlockEntity::tick;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.getBlockEntity(pos) instanceof CragbeastEggBlockEntity egg) {
            egg.startIncubation();
        }
    }
}
