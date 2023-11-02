package me.langyue.equipmentstandard.api;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface ItemStackAccessor {

    Multimap<Attribute, AttributeModifier> es$getOriginalAttributeModifiers(EquipmentSlot arg);
}
