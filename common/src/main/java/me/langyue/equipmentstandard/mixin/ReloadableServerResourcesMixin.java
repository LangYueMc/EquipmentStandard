package me.langyue.equipmentstandard.mixin;

import me.langyue.equipmentstandard.MixinUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {

    @Redirect(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/tags/TagManager$LoadResult;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;bindTags(Ljava/util/Map;)V"))
    private static <T> void updateRegistryTags(Registry<T> instance, Map<TagKey<T>, List<Holder<T>>> tagKeyListMap) {
        instance.bindTags(MixinUtils.bindTagsMixin(tagKeyListMap, instance.holders()));
    }
}
