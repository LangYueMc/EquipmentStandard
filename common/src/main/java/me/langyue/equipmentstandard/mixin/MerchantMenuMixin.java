package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.EquipmentComponentsAccessor;
import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MerchantMenu.class)
public abstract class MerchantMenuMixin {

    @Shadow
    @Mutable
    @Final
    private Merchant trader;

    @ModifyVariable(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/MerchantMenu;moveItemStackTo(Lnet/minecraft/world/item/ItemStack;IIZ)Z", ordinal = 0), ordinal = 1)
    private ItemStack quickMoveStackMixin(ItemStack original) {
        if (!EquipmentStandard.CONFIG.appliedToMerchant) return original;
        if (trader instanceof LivingEntity livingEntity && trader.getTradingPlayer() instanceof ServerPlayer) {
            ProficiencyAccessor proficiencyAccessor = (ProficiencyAccessor) livingEntity;
            // 批量产品难免瑕疵, 说不定还是进的货，熟练度设置成 0 吧（很合理）
            if (ModifierUtils.setItemStackAttribute(original)
                    && EquipmentStandard.RANDOM.nextDouble() < 0.34)
                // 批量购买的熟练度也不加那么多了, 大概三分之一
                proficiencyAccessor.es$incrementProficiency();
            ((EquipmentComponentsAccessor) (Object) original).es$updateScore();    // 计算评分
        }
        return original;
    }
}
