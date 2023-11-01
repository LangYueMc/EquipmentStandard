package me.langyue.equipmentstandard.api.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

import java.security.InvalidParameterException;

public class ItemVerifier {

    private final String id;
    private final String namespace;
    private final TagKey<Item> tag;

    public ItemVerifier(String verifier) {
        if (StringUtils.isEmpty(verifier)) {
            throw new InvalidParameterException("verifier: " + verifier);
        }
        if (verifier.startsWith("#")) {
            this.tag = TagKey.create(Registries.ITEM, new ResourceLocation(verifier.substring(1)));
            this.id = null;
            this.namespace = null;
        } else if (verifier.startsWith("@")) {
            this.namespace = verifier.substring(1);
            this.id = null;
            this.tag = null;
        } else {
            this.id = verifier;
            this.namespace = null;
            this.tag = null;
        }
    }

    public boolean isValid(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        } else if (id != null) {
            return BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString().equals(id);
        } else if (namespace != null) {
            return BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getNamespace().equals(namespace);
        } else if (tag != null) {
            return itemStack.is(tag);
        }
        return false;
    }
}
