package me.langyue.equipmentstandard.api.data;

import dev.architectury.platform.Platform;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

public class EquipmentTemplate {
    private final List<String> modLoaded;
    private final List<ItemVerifier> verifiers;
    private final List<ItemVerifier> excludes;
    private final List<Attribute> attributes;
    private final Set<Attribute.Slot> slots;

    public EquipmentTemplate(List<String> modLoaded, List<ItemVerifier> verifiers, List<ItemVerifier> excludes, List<Attribute> attributes, Set<Attribute.Slot> slots) {
        this.modLoaded = modLoaded;
        this.verifiers = verifiers;
        this.excludes = excludes;
        this.attributes = attributes;
        this.slots = slots;
    }

    public boolean init() {
        if (this.modLoaded == null || this.modLoaded.isEmpty() || this.modLoaded.stream().anyMatch(Platform::isModLoaded)) {
            this.attributes.forEach(attribute -> attribute.init(this));
            return true;
        }
        return false;
    }

    public boolean isValid(ItemStack itemStack) {
        if (excludes != null && excludes.stream().anyMatch(it -> it.isValid(itemStack))) {
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
