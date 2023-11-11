package me.langyue.equipmentstandard.data;

import com.google.gson.Gson;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * 仅加载 equipment_standard 目录下的资源
 *
 * @param <T>
 */
abstract class BaseDataLoader<T> extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
    private final Gson gson;
    private final String directory;
    private final Class<T> tClass;

    protected BaseDataLoader(Gson gson, String string) {
        this.gson = gson;
        this.directory = string;
        this.tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    protected @NotNull Map<ResourceLocation, T> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        HashMap<ResourceLocation, T> map = new HashMap<>();
        FileToIdConverter fileToIdConverter = FileToIdConverter.json(this.directory);
        for (Map.Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation resourceLocation = entry.getKey();
            if (!resourceLocation.getNamespace().equals(EquipmentStandard.MOD_ID)) {
                continue;
            }
            ResourceLocation id = fileToIdConverter.fileToId(resourceLocation);
            try (BufferedReader reader = entry.getValue().openAsReader()) {
                map.put(id, GsonHelper.fromJson(gson, reader, tClass));
            } catch (Throwable throwable) {
                EquipmentStandard.LOGGER.error("Couldn't parse data file {} from {}", id, resourceLocation, throwable);
            }
        }
        return map;
    }
}
