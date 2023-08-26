package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.api.EquipmentComponentsAccessor;
import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import me.langyue.equipmentstandard.api.data.EquipmentComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements EquipmentComponentsAccessor {

    private final ItemStack _this = (ItemStack) (Object) this;

    private boolean _updateScore = false;

    @Inject(method = "getMaxDamage", at = @At("TAIL"), cancellable = true)
    private void getMaxDamageMixin(CallbackInfoReturnable<Integer> info) {
        if (!_this.isDamageable()) return;
        info.setReturnValue(ModifierUtils.getMaxDamage(_this, info.getReturnValue()));
    }

    @Override
    public EquipmentComponents getComponents() {
        return EquipmentComponents.fromItem(_this);
    }

    @Override
    public String getMaker() {
        EquipmentComponents components = getComponents();
        return components == null ? null : components.getMaker();
    }

    @Override
    public void setMaker(PlayerEntity player) {
        if (player.getWorld().isClient) return;
        if (getComponents() == null) {
            // 仅能设置一次
            ProficiencyAccessor proficiencyAccessor = (ProficiencyAccessor) player;
            new EquipmentComponents(player.getDisplayName().getString(), proficiencyAccessor.getProficiency()).save(_this);
        }
    }

    @Override
    public Integer getScore() {
        if (!_updateScore) updateScore();
        EquipmentComponents components = this.getComponents();
        return components == null ? null : components.getScore();
    }

    @Override
    public void updateScore() {
        EquipmentComponents components = getComponents();
        if (components == null) {
            components = new EquipmentComponents();
        }
        components.setScore((int) Math.max(ModifierUtils.getScore(_this), 0));
        components.save(_this);
        _updateScore = true;
    }
}
