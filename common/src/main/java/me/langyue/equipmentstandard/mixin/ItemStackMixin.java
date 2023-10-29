package me.langyue.equipmentstandard.mixin;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import me.langyue.equipmentstandard.api.EquipmentComponentsAccessor;
import me.langyue.equipmentstandard.api.ItemRarityManager;
import me.langyue.equipmentstandard.api.ModifierUtils;
import me.langyue.equipmentstandard.api.ProficiencyAccessor;
import me.langyue.equipmentstandard.api.data.EquipmentComponents;
import me.langyue.equipmentstandard.api.data.ItemRarity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements EquipmentComponentsAccessor {

    @Unique
    private final ItemStack es$this = (ItemStack) (Object) this;

    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
    private void hookGetAttributeModifiers(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir) {
        Multimap<Attribute, AttributeModifier> modifierMultimap = LinkedListMultimap.create(cir.getReturnValue());
        ModifierUtils.modify(es$this, equipmentSlot, modifierMultimap);
        cir.setReturnValue(modifierMultimap);
    }

    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    private void getHoverNameMixin(CallbackInfoReturnable<Component> cir) {
        var rarity = this.es$getItemRarity();
        if (rarity != null) {
            MutableComponent name = (MutableComponent) cir.getReturnValue();
            if (rarity.getFormatting() != null && rarity.getFormatting().length > 0) {
                name.withStyle(rarity.getFormatting());
            }
            MutableComponent prefix = rarity.getPrefix();
            if (prefix != null) {
                if (rarity.getFormatting() != null && rarity.getFormatting().length > 0) {
                    prefix.withStyle(rarity.getFormatting());
                }
                name = prefix.append(name);
            }
            cir.setReturnValue(name);
        }
    }

    @Inject(method = "getMaxDamage", at = @At("TAIL"), cancellable = true)
    private void getMaxDamageMixin(CallbackInfoReturnable<Integer> info) {
        if (!es$this.isDamageableItem()) return;
        info.setReturnValue(ModifierUtils.getMaxDamage(es$this, info.getReturnValue()));
    }

    @Override
    public EquipmentComponents es$getComponents() {
        return EquipmentComponents.fromItem(es$this);
    }

    @Override
    public String es$getMaker() {
        EquipmentComponents components = es$getComponents();
        return components == null ? null : components.getMaker();
    }

    @Override
    public void es$setMaker(Player player) {
        if (player.level().isClientSide()) return;
        if (es$getComponents() == null) {
            // 仅能设置一次
            ProficiencyAccessor proficiencyAccessor = (ProficiencyAccessor) player;
            new EquipmentComponents(player.getDisplayName().getString(), proficiencyAccessor.getProficiency()).save(es$this);
        }
    }

    @Override
    public Integer es$getScore() {
        EquipmentComponents components = this.es$getComponents();
        return components == null ? null : components.getScore();
    }

    @Override
    public void es$updateScore() {
        Integer score = ModifierUtils.getScore(es$this);
        if (score == null) {
            return;
        }
        EquipmentComponents components = es$getComponents();
        if (components == null) {
            components = new EquipmentComponents();
        }
        components.setScore(score);
        components.setRarity(ItemRarityManager.get(es$this, score));
        components.save(es$this);
    }

    @Override
    public ItemRarity.Rarity es$getItemRarity() {
        EquipmentComponents components = this.es$getComponents();
        return components == null ? null : components.getRarity();
    }
}
