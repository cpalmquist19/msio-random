package com.minesharesiorandom;

import com.minesharesiorandom.entity.CragbeastEntity;
import com.minesharesiorandom.entity.HatzegopteryxEntity;
import com.minesharesiorandom.registry.ModBlockEntities;
import com.minesharesiorandom.registry.ModBlocks;
import com.minesharesiorandom.registry.ModEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MinesharesioRandom implements ModInitializer {
    public static final String MOD_ID = "minesharesiorandom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntities.register();
        FabricDefaultAttributeRegistry.register(ModEntities.CRAGBEAST, CragbeastEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.HATZEGOPTERYX, HatzegopteryxEntity.createAttributes());
        LOGGER.info("Minesharesio Random is ready.");
    }
}
