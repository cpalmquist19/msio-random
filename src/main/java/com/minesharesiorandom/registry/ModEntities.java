package com.minesharesiorandom.registry;

import com.minesharesiorandom.MinesharesioRandom;
import com.minesharesiorandom.entity.CragbeastEntity;
import com.minesharesiorandom.entity.HatzegopteryxEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEntities {
    public static final EntityType<CragbeastEntity> CRAGBEAST = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MinesharesioRandom.MOD_ID, "cragbeast"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, CragbeastEntity::new)
                    .dimensions(EntityDimensions.fixed(32.0F, 52.0F))
                    .trackRangeBlocks(128)
                    .trackedUpdateRate(2)
                    .build()
    );

    public static final EntityType<HatzegopteryxEntity> HATZEGOPTERYX = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MinesharesioRandom.MOD_ID, "hatzegopteryx"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, HatzegopteryxEntity::new)
                    .dimensions(EntityDimensions.fixed(HatzegopteryxEntity.TARGET_WIDTH_BLOCKS, HatzegopteryxEntity.TARGET_HEIGHT_BLOCKS))
                    .trackRangeBlocks(80)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static final Item CRAGBEAST_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            new Identifier(MinesharesioRandom.MOD_ID, "cragbeast_spawn_egg"),
            new SpawnEggItem(CRAGBEAST, 0x40352C, 0xD36D42, new Item.Settings())
    );

    public static final Item HATZEGOPTERYX_SPAWN_EGG = Registry.register(
            Registries.ITEM,
            new Identifier(MinesharesioRandom.MOD_ID, "hatzegopteryx_spawn_egg"),
            new SpawnEggItem(HATZEGOPTERYX, 0x6B5B4F, 0xC4A882, new Item.Settings())
    );

    private ModEntities() {
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS)
                .register(entries -> {
                    entries.add(CRAGBEAST_SPAWN_EGG);
                    entries.add(HATZEGOPTERYX_SPAWN_EGG);
                });
    }
}
