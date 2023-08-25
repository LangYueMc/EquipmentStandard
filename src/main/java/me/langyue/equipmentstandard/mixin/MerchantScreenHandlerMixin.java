package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.EquipmentStandard;
import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MerchantScreenHandler.class)
public abstract class MerchantScreenHandlerMixin {

    @Shadow
    @Mutable
    @Final
    private Merchant merchant;

    @ModifyVariable(method = "transferSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/MerchantScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z", ordinal = 0), ordinal = 1)
    private ItemStack transferSlotMixin(ItemStack original) {
        if (merchant instanceof LivingEntity livingEntity) {
            ProficiencyAccessor proficiencyAccessor = (ProficiencyAccessor) livingEntity;
            // 批量产品难免瑕疵, 说不定还是进的货，熟练度设置成 0 吧（很合理）
            if (ModifierUtils.setItemStackAttribute(original, 0, 0)
                    && EquipmentStandard.RANDOM.nextDouble() < 0.34)
                // 批量购买的熟练度也不加那么多了, 大概三分之一
                proficiencyAccessor.incrementProficiency();
            original.updateScore();    // 计算评分
        }
        return original;
    }
}
