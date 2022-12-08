package me.langyue.equipmentstandard.data;

import net.minecraft.item.ItemStack;

import java.util.List;

public class EquipmentTemplate {
    private final List<ItemVerifier> verifiers;
    private final List<ItemVerifier> exclude;
    private final List<Attribute> attributes;

    public EquipmentTemplate(List<ItemVerifier> verifiers, List<ItemVerifier> exclude, List<Attribute> attributes) {
        this.verifiers = verifiers;
        this.exclude = exclude;
        this.attributes = attributes;
    }

    public boolean isValid(ItemStack itemStack) {
        if (exclude != null &&exclude.stream().anyMatch(it -> it.isValid(itemStack))) {
            return false;
        }
        return verifiers.stream().anyMatch(it -> it.isValid(itemStack));
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }
}
