package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.api.data.EquipmentComponents;
import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "onCraft", at = @At("TAIL"))
    private void onCraftMixin(ItemStack stack, World world, PlayerEntity player, CallbackInfo info) {
        if (!world.isClient && !stack.isEmpty()) {
            if (ModifierUtils.setItemStackAttribute(stack, player)) {
                ProficiencyAccessor proficiencyAccessor = (ProficiencyAccessor) player;
                new EquipmentComponents(player.getDisplayName().getString(), proficiencyAccessor.getProficiency()).save(stack);
                proficiencyAccessor.incrementProficiency();
            }
        }
    }

}
