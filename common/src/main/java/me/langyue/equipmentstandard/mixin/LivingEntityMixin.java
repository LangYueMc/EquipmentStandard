package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.world.entity.ai.attributes.ESAttributes;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ProficiencyAccessor {

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public abstract boolean isDeadOrDying();

    @Shadow
    @Final
    private static EntityDataAccessor<Float> DATA_HEALTH_ID;
    /**
     * 制作熟练度
     */
    @Unique
    private int es$proficiency;

    @Unique
    private float es$maxHealth;

    /**
     * 溢出的血量，防止切换带有增加最高生命上限的物品时产生黑血，浪费饱食度
     * 没必要存起来了，仅当前进程有效
     */
    @Unique
    private float es$overfullHealth;

    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void createLivingAttributesMixin(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        ESAttributes.createLivingAttributes(cir.getReturnValue());
    }

    /**
     * Minecraft 的代码在加载实体时设置生命值时会有最大生命值限制，但刚加载的实体并没有应用属性修改器，就会导致如果有加生命上限的属性时实体刚加载会有黑心，
     * 每次客户端登陆会往服务端发送同步血量请求
     * <p>
     * 原作者 @Globox1997
     */
    @Redirect(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void readAdditionalSaveDataMixin(LivingEntity livingEntity, float health) {
        this.entityData.set(DATA_HEALTH_ID, health);
    }

    @Inject(method = "collectEquipmentChanges", at = @At("HEAD"))
    private void collectEquipmentChangesMixin(CallbackInfoReturnable<@Nullable Map<EquipmentSlot, ItemStack>> cir) {
        if (this.isDeadOrDying()) return;
        es$maxHealth = this.getMaxHealth();
    }

    @Inject(method = "collectEquipmentChanges", at = @At("TAIL"))
    private void resetHealth(CallbackInfoReturnable<@Nullable Map<EquipmentSlot, ItemStack>> cir) {
        if (!this.isDeadOrDying() && this.getMaxHealth() - es$maxHealth != 0) {
            float health = this.getHealth() + es$overfullHealth;   // 将溢出的血量添加上之后设置实体血量
            this.setHealth(health);
            es$overfullHealth = Math.max(0, health - this.getHealth());
            if (((LivingEntity) (Object) this) instanceof ServerPlayer serverPlayer) {
                serverPlayer.resetSentInfo();
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    private void addAdditionalSaveDataMixin(CompoundTag nbt, CallbackInfo ci) {
        if (this instanceof Merchant || ((LivingEntity) (Object) this) instanceof Player) {
            nbt.putInt(NBT_KEY, es$proficiency);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    private void readAdditionalSaveDataMixin(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains(NBT_KEY, CompoundTag.TAG_INT)) {
            es$proficiency = nbt.getInt(NBT_KEY);
        }
    }

    @Override
    public int getProficiency() {
        return es$proficiency;
    }

    @Override
    public int incrementProficiency() {
        return ++es$proficiency;
    }
}
