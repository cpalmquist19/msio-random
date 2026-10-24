package com.minesharesiorandom.registry;

import com.minesharesiorandom.MinesharesioRandom;
import com.minesharesiorandom.block.CragbeastEggBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final Block CRAGBEAST_EGG = Registry.register(
            Registries.BLOCK,
            new Identifier(MinesharesioRandom.MOD_ID, "cragbeast_egg"),
            new CragbeastEggBlock(AbstractBlock.Settings.create()
                    .strength(0.5F)
                    .sounds(BlockSoundGroup.STONE))
    );

    public static final Item CRAGBEAST_EGG_ITEM = Registry.register(
            Registries.ITEM,
            new Identifier(MinesharesioRandom.MOD_ID, "cragbeast_egg"),
            new BlockItem(CRAGBEAST_EGG, new Item.Settings())
    );

    private ModBlocks() {
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL)
                .register(entries -> entries.add(CRAGBEAST_EGG_ITEM));
    }
}
