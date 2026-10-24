package com.minesharesiorandom.entity;

import com.minesharesiorandom.registry.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HatzegopteryxEntity extends HostileEntity implements GeoEntity {
    /** Vertical extent of the Blockbench model in pixels (16 pixels = 1 block at base scale). */
    public static final float MODEL_HEIGHT_UNITS = 72F;
    public static final float MODEL_WIDTH_UNITS = 32F;
    public static final float TARGET_HEIGHT_BLOCKS = 4.6F;
    public static final float RENDER_SCALE = TARGET_HEIGHT_BLOCKS / (MODEL_HEIGHT_UNITS / 16F);
    public static final float TARGET_WIDTH_BLOCKS = MODEL_WIDTH_UNITS / MODEL_HEIGHT_UNITS * TARGET_HEIGHT_BLOCKS;

    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation FLY_ANIMATION = RawAnimation.begin().thenLoop("fly");
    private static final RawAnimation ATTACK_ANIMATION = RawAnimation.begin().thenPlay("attack");
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public HatzegopteryxEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 240.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0)
                .add(EntityAttributes.GENERIC_ARMOR, 0.5)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new AttackGoal(this));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.9));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 32.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
    }

    @Override
    public boolean tryAttack(net.minecraft.entity.Entity target) {
        this.triggerAnim("action", "attack");
        return super.tryAttack(target);
    }

    private PlayState movementPredicate(AnimationState<HatzegopteryxEntity> state) {
        if (!this.isOnGround() && !this.isTouchingWater()) {
            state.setAnimation(FLY_ANIMATION);
        } else if (state.isMoving()) {
            state.setAnimation(WALK_ANIMATION);
        } else {
            state.setAnimation(IDLE_ANIMATION);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementPredicate)
                .triggerableAnim("attack", ATTACK_ANIMATION));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }

    public static EntityType<HatzegopteryxEntity> type() {
        return ModEntities.HATZEGOPTERYX;
    }
}
