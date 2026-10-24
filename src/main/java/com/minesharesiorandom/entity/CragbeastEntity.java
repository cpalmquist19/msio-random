package com.minesharesiorandom.entity;

import com.minesharesiorandom.registry.ModEntities;
import com.minesharesiorandom.registry.ModBlocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CragbeastEntity extends HostileEntity implements GeoEntity {
    private static final long BABY_GROWTH_TIME = 864_000L;
    private static final int BREEDING_WINDOW_TICKS = 600;
    private static final int BREEDING_COOLDOWN_TICKS = 6_000;
    private static final TrackedData<Boolean> BABY = DataTracker.registerData(CragbeastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK_ANIMATION = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation STOMP_ANIMATION = RawAnimation.begin().thenPlay("stomp");
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossBar bossBar = new ServerBossBar(
            Text.literal("Cragbeast"),
            BossBar.Color.RED,
            BossBar.Style.PROGRESS
    );
    private long maturationTime;
    private int loveTicks;
    private int breedingCooldown;

    public CragbeastEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 100;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1024.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.18)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 250.0)
                .add(EntityAttributes.GENERIC_ARMOR, 30.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 4.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 96.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BABY, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new AttackGoal(this));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 32.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, VillagerEntity.class, true));
    }

    @Override
    public boolean tryAttack(net.minecraft.entity.Entity target) {
        this.triggerAnim("movement", "stomp");
        return super.tryAttack(target);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!this.isBaby() && this.breedingCooldown == 0 && stack.isOf(Items.BEACON)) {
            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }

            this.loveTicks = BREEDING_WINDOW_TICKS;
            this.getWorld().sendEntityStatus(this, (byte) 18);
            return ActionResult.success(this.getWorld().isClient);
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient) {
            return;
        }

        if (this.isBaby() && this.getWorld().getTime() >= this.maturationTime) {
            this.setBaby(false);
        }

        this.updateBossBar();

        if (this.loveTicks > 0) {
            this.loveTicks--;
        }

        if (this.breedingCooldown > 0) {
            this.breedingCooldown--;
        }

        if (this.age % 20 == 0 && this.canBreed()) {
            this.tryLayEgg();
        }
    }

    private boolean canBreed() {
        return !this.isBaby() && this.loveTicks > 0 && this.breedingCooldown == 0;
    }

    private void tryLayEgg() {
        CragbeastEntity mate = this.getWorld().getClosestEntity(
                CragbeastEntity.class,
                null,
                this,
                this.getX(),
                this.getY(),
                this.getZ(),
                this.getBoundingBox().expand(12.0)
        );

        if (mate == null || !mate.canBreed() || this.getUuid().compareTo(mate.getUuid()) >= 0) {
            return;
        }

        this.dropItem(ModBlocks.CRAGBEAST_EGG_ITEM);
        this.finishBreeding();
        mate.finishBreeding();
    }

    private void finishBreeding() {
        this.loveTicks = 0;
        this.breedingCooldown = BREEDING_COOLDOWN_TICKS;
        this.getWorld().sendEntityStatus(this, (byte) 7);
    }

    public boolean isBaby() {
        return this.dataTracker.get(BABY);
    }

    public void setBaby(boolean baby) {
        this.dataTracker.set(BABY, baby);
        if (!this.getWorld().isClient) {
            if (baby) {
                this.maturationTime = this.getWorld().getTime() + BABY_GROWTH_TIME;
                this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(128.0);
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(50.0);
                this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).setBaseValue(0.0);
                this.setHealth(1024.0F);
            } else {
                this.maturationTime = 0;
                this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(1024.0);
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(50.0);
                this.setHealth(1024.0F);
            }
            this.updateBossBar();
        }
        this.calculateDimensions();
    }

    @Override
    public net.minecraft.entity.EntityDimensions getDimensions(net.minecraft.entity.EntityPose pose) {
        return super.getDimensions(pose).scaled(this.isBaby() ? 0.35F : 1.0F);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Baby", this.isBaby());
        nbt.putLong("MaturationTime", this.maturationTime);
        nbt.putInt("LoveTicks", this.loveTicks);
        nbt.putInt("BreedingCooldown", this.breedingCooldown);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(BABY, nbt.getBoolean("Baby"));
        this.maturationTime = nbt.getLong("MaturationTime");
        this.loveTicks = nbt.getInt("LoveTicks");
        this.breedingCooldown = nbt.getInt("BreedingCooldown");
        this.calculateDimensions();
        this.updateBossBar();
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    private void updateBossBar() {
        this.bossBar.setName(Text.literal(this.isBaby() ? "Baby Cragbeast" : "Cragbeast"));
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }

    private PlayState movementPredicate(AnimationState<CragbeastEntity> state) {
        state.setAnimation(state.isMoving() ? WALK_ANIMATION : IDLE_ANIMATION);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementPredicate)
                .triggerableAnim("stomp", STOMP_ANIMATION));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.animationCache;
    }

    public static EntityType<CragbeastEntity> type() {
        return ModEntities.CRAGBEAST;
    }
}
