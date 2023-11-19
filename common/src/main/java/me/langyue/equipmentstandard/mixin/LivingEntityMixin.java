package me.langyue.equipmentstandard.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import me.langyue.equipmentstandard.world.entity.ai.attributes.ESAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ProficiencyAccessor {

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public abstract boolean isDeadOrDying();

    /**
     * 制作熟练度
     */
    @Unique
    private int es$proficiency;

    /**
     * 实体实际保存才 NBT 里的血量，此处保存后加载防止黑血
     */
    @Unique
    private Float es$health;

    /**
     * 溢出的血量，防止切换带有增加最高生命上限的物品时产生黑血，浪费饱食度
     * 没必要存起来了，仅当前进程有效
     */
    @Unique
    private float es$overfullHealth;

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void createLivingAttributesMixin(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        ESAttributes.createLivingAttributes(cir.getReturnValue());
    }

    /**
     * Minecraft 的代码在加载实体时设置生命值时会有最大生命值限制，但刚加载的实体并没有应用属性修改器，就会导致如果有加生命上限的属性时实体刚加载会有黑心，
     */
    @Inject(method = "readAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void readEntityHealth(CompoundTag tag, CallbackInfo ci) {
        var health = tag.getFloat("Health");
        if (health > getMaxHealth() && health > 0) {
            es$health = health;
        }
    }

    @Inject(method = "collectEquipmentChanges", at = @At("HEAD"))
    private void recordMaxHealth(CallbackInfoReturnable<@Nullable Map<EquipmentSlot, ItemStack>> cir, @Share("maxHealth") LocalFloatRef maxHealth) {
        if (this.isDeadOrDying()) return;
        maxHealth.set(this.getMaxHealth());
    }

    @Inject(method = "collectEquipmentChanges", at = @At("TAIL"))
    private void resetHealth(
            CallbackInfoReturnable<@Nullable Map<EquipmentSlot, ItemStack>> cir,
            @Share("maxHealth") LocalFloatRef maxHealth
    ) {
        if (this.isDeadOrDying()) return;
        if (es$health != null && es$health != this.getHealth()) {
            // 不为空代表是从 NBT 中加载来的，也就是刚加载
            this.setHealth(es$health);
            es$health = null;
            // 刚加载的实体是没有溢出的血量的
            return;
        }
        if (!this.isDeadOrDying() && this.getMaxHealth() - maxHealth.get() != 0) {
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
