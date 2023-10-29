package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.api.EquipmentComponentsAccessor;
import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantResultSlot.class)
public class MerchantResultSlotMixin {

    @Shadow
    @Mutable
    @Final
    private Merchant merchant;

    @Inject(method = "onTake", at = @At(value = "HEAD"))
    private void transferSlotMixin(Player player, ItemStack stack, CallbackInfo ci) {
        // 批量购买的装备是直接塞进背包的，传到这里的是复制品
        if (merchant instanceof LivingEntity livingEntity && player instanceof ServerPlayer) {
            ProficiencyAccessor proficiencyAccessor = (ProficiencyAccessor) livingEntity;
            int proficiency = proficiencyAccessor.getProficiency() / 100;   // 村民熟练度加成 100 点 = 玩家 1 点
            if (ModifierUtils.setItemStackAttribute(stack, proficiency, 0))
                proficiencyAccessor.incrementProficiency();
            ((EquipmentComponentsAccessor) (Object) stack).es$updateScore();   // 计算评分
        }
    }
}
