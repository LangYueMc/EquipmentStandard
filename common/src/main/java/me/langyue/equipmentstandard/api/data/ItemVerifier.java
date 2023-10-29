package me.langyue.equipmentstandard.api.data;

import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

public class ItemVerifier {
    private final int order;

    private final ResourceLocation id;
    private final TagKey<Item> tag;

    public ItemVerifier(String id, String tag) {
        if (StringUtils.isEmpty(id)) {
            this.id = null;
            this.tag = TagKey.create(Registries.ITEM, new ResourceLocation(tag));
            this.order = 1;
        } else {
            this.id = ResourceLocation.tryParse(id);
            this.tag = null;
            this.order = 0;
        }
        if (this.id == null && this.tag == null) {
            throw new ResourceLocationException("id: " + id + ", tag: " + tag);
        }
    }

    public int getOrder() {
        return order;
    }

    public boolean isValid(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        } else if (id != null) {
            return BuiltInRegistries.ITEM.getKey(itemStack.getItem()).equals(id);
        } else if (tag != null) {
            return itemStack.is(tag);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode() * 17 + (tag == null ? 0 : tag.hashCode());
    }
}
