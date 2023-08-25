package me.langyue.equipmentstandard.data;

import com.google.gson.Gson;
import me.langyue.equipmentstandard.EquipmentStandard;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.util.Identifier;

public abstract class BaseDataLoader extends JsonDataLoader implements SimpleSynchronousResourceReloadListener {
    protected final String type;

    public BaseDataLoader(Gson gson, String type) {
        super(gson, type);
        this.type = type;
    }

    @Override
    public Identifier getFabricId() {
        return EquipmentStandard.createIdentifier(this.type);
    }
}
