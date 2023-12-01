package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.api.EquipmentComponentsAccessor;
import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "onCraftedBy", at = @At("TAIL"))
    private void onCraftedByMixin(ItemStack itemStack, Level level, Player player, CallbackInfo ci) {
        if (level.isClientSide() || itemStack.isEmpty()) return;
        if (ModifierUtils.setItemStackAttribute(itemStack, player)) {
            ProficiencyAccessor proficiencyAccessor = (ProficiencyAccessor) player;
            ((EquipmentComponentsAccessor) (Object) itemStack).es$setMaker(player);
            // 熟练度
            String name = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();
            // 部分不加熟练度的
            if (Stream.of("wooden", "stone", "leather", "chainmail", "turtle").noneMatch(name::contains)) {
                proficiencyAccessor.es$incrementProficiency();
            }
        }
        ((EquipmentComponentsAccessor) (Object) itemStack).es$updateScore();    // 计算评分
    }

}
