package com.minesharesiorandom.client;

import com.minesharesiorandom.MinesharesioRandom;
import com.minesharesiorandom.entity.HatzegopteryxEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class HatzegopteryxRenderer extends GeoEntityRenderer<HatzegopteryxEntity> {
    public HatzegopteryxRenderer(EntityRendererFactory.Context context) {
        super(context, new HatzegopteryxModel());
        this.withScale(HatzegopteryxEntity.RENDER_SCALE);
        this.shadowRadius = HatzegopteryxEntity.TARGET_WIDTH_BLOCKS * 0.5F;
    }

    private static final class HatzegopteryxModel extends GeoModel<HatzegopteryxEntity> {
        @Override
        public Identifier getModelResource(HatzegopteryxEntity entity) {
            return new Identifier(MinesharesioRandom.MOD_ID, "geo/hatzegopteryx.geo.json");
        }

        @Override
        public Identifier getTextureResource(HatzegopteryxEntity entity) {
            return new Identifier(MinesharesioRandom.MOD_ID, "textures/entity/hatzegopteryx.png");
        }

        @Override
        public Identifier getAnimationResource(HatzegopteryxEntity entity) {
            return new Identifier(MinesharesioRandom.MOD_ID, "animations/hatzegopteryx.animation.json");
        }
    }
}
