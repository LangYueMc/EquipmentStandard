package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.village.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TradeOutputSlot.class)
public class TradeOutputSlotMixin {

    @Shadow
    @Mutable
    @Final
    private Merchant merchant;

    @Inject(method = "onTakeItem", at = @At(value = "HEAD"))
    private void transferSlotMixin(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        // 批量购买的装备是直接塞进背包的，传到这里的是复制品
        if (merchant instanceof LivingEntity livingEntity) {
            ProficiencyAccessor proficiencyAccessor = (ProficiencyAccessor) livingEntity;
            int proficiency = proficiencyAccessor.getProficiency() / 100;   // 村民熟练度加成 100 点 = 玩家 1 点
            if (ModifierUtils.setItemStackAttribute(stack, proficiency, 0))
                proficiencyAccessor.incrementProficiency();
        }
    }
}
