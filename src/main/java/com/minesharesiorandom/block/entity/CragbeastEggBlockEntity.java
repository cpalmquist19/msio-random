package com.minesharesiorandom.block.entity;

import com.minesharesiorandom.entity.CragbeastEntity;
import com.minesharesiorandom.registry.ModBlockEntities;
import com.minesharesiorandom.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class CragbeastEggBlockEntity extends BlockEntity {
    // public static final long HATCH_TIME = 72_000L;
    public static final long HATCH_TIME = 200L;
    private long incubationStartedAt = -1L;

    public CragbeastEggBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRAGBEAST_EGG, pos, state);
    }

    public void startIncubation() {
        if (this.world != null && !this.world.isClient && this.incubationStartedAt < 0) {
            this.incubationStartedAt = this.world.getTime();
            this.markDirty();
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, CragbeastEggBlockEntity egg) {
        if (world.isClient) {
            return;
        }

        if (egg.incubationStartedAt < 0) {
            egg.startIncubation();
            return;
        }

        if (world.getTime() - egg.incubationStartedAt < HATCH_TIME) {
            return;
        }

        CragbeastEntity baby = ModEntities.CRAGBEAST.create(world);
        if (baby == null) {
            return;
        }

        baby.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, world.random.nextFloat() * 360.0F, 0.0F);
        baby.setBaby(true);
        world.removeBlock(pos, false);
        world.spawnEntity(baby);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("IncubationStartedAt", this.incubationStartedAt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.incubationStartedAt = nbt.getLong("IncubationStartedAt");
    }
}
