package com.minesharesiorandom.registry;

import com.minesharesiorandom.MinesharesioRandom;
import com.minesharesiorandom.block.entity.CragbeastEggBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlockEntities {
    public static final BlockEntityType<CragbeastEggBlockEntity> CRAGBEAST_EGG = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(MinesharesioRandom.MOD_ID, "cragbeast_egg"),
            FabricBlockEntityTypeBuilder.create(CragbeastEggBlockEntity::new, ModBlocks.CRAGBEAST_EGG).build()
    );

    private ModBlockEntities() {
    }

    public static void register() {
        // Forces static registration during mod initialization.
    }
}
