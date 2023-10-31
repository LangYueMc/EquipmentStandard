package me.langyue.equipmentstandard.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.langyue.equipmentstandard.api.CustomTag;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> implements WritableRegistry<T> {

    @ModifyVariable(method = "bindTags", at = @At("HEAD"), argsOnly = true)
    private Map<TagKey<T>, List<Holder<T>>> bindTagsMixin(Map<TagKey<T>, List<Holder<T>>> map) {
        if (map instanceof HashMap<TagKey<T>, List<Holder<T>>>) {
            // 客户端是 HashMap，源码 ClientCommonPacketListenerImpl.updateTagsForRegistry
            // 服务端 ImmutableCollections.MapN，源码 ReloadableServerResources.updateRegistryTags
            // 这里如果是客户端直接返回
            return map;
        }
        Map<TagKey<T>, List<Holder<T>>> result = Maps.newHashMap(map);
        this.holders().forEach(entry -> {
            if (entry.value() instanceof Item item) {
                CustomTag.getAll().forEach(customTag -> {
                    if (customTag.test(item)) {
                        TagKey tagKey = customTag.getTagKey();
                        List<Holder<T>> entryList;
                        if (result.containsKey(tagKey)) {
                            entryList = result.get(tagKey);
                            if (!(entryList instanceof LinkedList<Holder<T>>)) {
                                entryList = Lists.newLinkedList(entryList);
                            }
                        } else {
                            entryList = new LinkedList<>();
                            result.put(tagKey, entryList);
                        }
                        entryList.add(entry);
                    }
                });
            }
        });
        return result;
    }
}
