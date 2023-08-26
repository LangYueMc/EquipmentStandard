package me.langyue.equipmentstandard.data;

import me.langyue.equipmentstandard.api.data.Attribute;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Set;

public class EquipmentTemplate {
    private final List<ItemVerifier> verifiers;
    private final List<ItemVerifier> exclude;
    private final List<Attribute> attributes;
    private final Set<Attribute.Slot> slots;

    public EquipmentTemplate(List<ItemVerifier> verifiers, List<ItemVerifier> exclude, List<Attribute> attributes, Set<Attribute.Slot> slots) {
        this.verifiers = verifiers;
        this.exclude = exclude;
        this.attributes = attributes;
        this.slots = slots;
    }

    public void init() {
        this.attributes.forEach(attribute -> attribute.init(this));
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

    public Set<Attribute.Slot> getSlots() {
        return slots;
    }
}
