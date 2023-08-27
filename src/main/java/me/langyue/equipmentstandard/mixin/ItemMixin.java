package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "onCraft", at = @At("TAIL"))
    private void onCraftMixin(ItemStack stack, World world, PlayerEntity player, CallbackInfo info) {
        if (!world.isClient && !stack.isEmpty()) {
            if (ModifierUtils.setItemStackAttribute(stack, player)) {
                ProficiencyAccessor proficiencyAccessor = (ProficiencyAccessor) player;
                stack.setMaker(player);
                // 熟练度
                String name = Registries.ITEM.getId(stack.getItem()).getPath();
                // 部分不加熟练度的
                if (Stream.of("wooden", "stone", "leather", "chainmail", "turtle").noneMatch(name::contains)) {
                    proficiencyAccessor.incrementProficiency();
                }
            }
            stack.updateScore();    // 计算评分
        }
    }

}
