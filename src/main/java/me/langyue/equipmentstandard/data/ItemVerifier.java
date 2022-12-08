package me.langyue.equipmentstandard.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

public class ItemVerifier {
    private final int order;

    private final Identifier id;
    private final TagKey<Item> tag;

    public ItemVerifier(String id, String tag) {
        if (StringUtils.isEmpty(id)) {
            this.id = null;
            this.tag = TagKey.of(Registry.ITEM_KEY, new Identifier(tag));
            this.order = 1;
        } else {
            this.id = Identifier.tryParse(id);
            this.tag = null;
            this.order = 0;
        }
        if (this.id == null && this.tag == null) {
            throw new InvalidIdentifierException("id: " + id + ", tag: " + tag);
        }
    }

    public int getOrder() {
        return order;
    }

    public boolean isValid(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        } else if (id != null) {
            return Registry.ITEM.getId(itemStack.getItem()).equals(id);
        } else if (tag != null) {
            return itemStack.isIn(tag);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode() * 17 + (tag == null ? 0 : tag.hashCode());
    }
}
