package com.minesharesiorandom.client;

import com.minesharesiorandom.MinesharesioRandom;
import com.minesharesiorandom.entity.CragbeastEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class CragbeastRenderer extends GeoEntityRenderer<CragbeastEntity> {
    public CragbeastRenderer(EntityRendererFactory.Context context) {
        super(context, new CragbeastModel());
        this.shadowRadius = 16.0F;
    }

    private static final class CragbeastModel extends GeoModel<CragbeastEntity> {
        @Override
        public Identifier getModelResource(CragbeastEntity entity) {
            return new Identifier(
                    MinesharesioRandom.MOD_ID,
                    entity.isBaby() ? "geo/cragbeast_baby.geo.json" : "geo/cragbeast.geo.json"
            );
        }

        @Override
        public Identifier getTextureResource(CragbeastEntity entity) {
            return new Identifier(
                    MinesharesioRandom.MOD_ID,
                    entity.isBaby() ? "textures/entity/cragbeast_baby.png" : "textures/entity/cragbeast.png"
            );
        }

        @Override
        public Identifier getAnimationResource(CragbeastEntity entity) {
            return new Identifier(
                    MinesharesioRandom.MOD_ID,
                    entity.isBaby() ? "animations/cragbeast_baby.animation.json" : "animations/cragbeast.animation.json"
            );
        }
    }
}
