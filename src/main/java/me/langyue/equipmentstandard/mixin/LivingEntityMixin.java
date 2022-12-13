package me.langyue.equipmentstandard.mixin;

import com.google.common.collect.Multimap;
import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ProficiencyAccessor {

    private final LivingEntity entity = (LivingEntity) (Object) this;

    @Shadow
    @Mutable
    @Final
    private static TrackedData<Float> HEALTH;

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract boolean isDead();

    private int proficiency;

    private boolean resetHealth;

    /**
     * 溢出的血量，防止切换带有增加最高生命上限的物品时产生黑血，浪费饱食度
     */
    private float overfullHealth;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * Minecraft 的代码在加载实体时设置生命值时会有最大生命值限制，但刚加载的实体并没有应用属性修改器，就会导致如果有加生命上限的属性时实体刚加载会有黑心
     * 部分代码来自 @Globox1997
     *
     * @see MerchantScreenHandlerMixin
     */
    @Redirect(method = "readCustomDataFromNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
    private void readCustomDataFromNbtMixin(LivingEntity livingEntity, float health) {
        this.dataTracker.set(HEALTH, health);
    }

    @Inject(method = "getEquipmentChanges", at = @At("HEAD"))
    private void getEquipmentChangesMixin(CallbackInfoReturnable<@Nullable Map<EquipmentSlot, ItemStack>> cir) {
        resetHealth = false;
    }

    @Redirect(method = "getEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;removeModifiers(Lcom/google/common/collect/Multimap;)V"))
    private void onRemoveModifiers(AttributeContainer instance, Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers) {
        resetHealth |= attributeModifiers.entries().stream()
                .anyMatch(entry -> entry.getKey().equals(Registry.ATTRIBUTE.get(new Identifier("generic.max_health")))
                        && entry.getValue().getName().equals(ModifierUtils.MODIFIER_NAME));
        instance.removeModifiers(attributeModifiers);
    }

    @Redirect(method = "getEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;addTemporaryModifiers(Lcom/google/common/collect/Multimap;)V"))
    private void onAddTemporaryModifiers(AttributeContainer instance, Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers) {
        resetHealth |= attributeModifiers.entries().stream()
                .anyMatch(entry -> entry.getKey().equals(Registry.ATTRIBUTE.get(new Identifier("generic.max_health")))
                        && entry.getValue().getName().equals(ModifierUtils.MODIFIER_NAME));
        instance.addTemporaryModifiers(attributeModifiers);
    }

    @Inject(method = "getEquipmentChanges", at = @At("TAIL"))
    private void resetHealth(CallbackInfoReturnable<@Nullable Map<EquipmentSlot, ItemStack>> cir) {
        if (resetHealth && !this.isDead()) {

            float health = this.getHealth() + overfullHealth;   // 将溢出的血量添加上之后设置实体血量
            this.setHealth(health);
            overfullHealth = Math.max(0, health - this.getHealth());
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                serverPlayerEntity.markHealthDirty();
//                ServerPacket.sendSyncHealthPacket(serverPlayerEntity);
            }
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    private void writeCustomDataToNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (livingEntity instanceof MerchantEntity || livingEntity instanceof PlayerEntity) {
            nbt.putInt(NBT_KEY, proficiency);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void readCustomDataFromNbtMixin(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(NBT_KEY, NbtElement.INT_TYPE)) {
            proficiency = nbt.getInt(NBT_KEY);
        }
    }

    @Override
    public int getProficiency() {
        return proficiency;
    }

    @Override
    public int incrementProficiency() {
        return ++proficiency;
    }
}
