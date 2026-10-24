package com.minesharesiorandom.client;

import com.minesharesiorandom.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public final class MinesharesioRandomClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.CRAGBEAST, CragbeastRenderer::new);
        EntityRendererRegistry.register(ModEntities.HATZEGOPTERYX, HatzegopteryxRenderer::new);
    }
}
