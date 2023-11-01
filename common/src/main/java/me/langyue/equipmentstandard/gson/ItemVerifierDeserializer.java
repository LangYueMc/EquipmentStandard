package me.langyue.equipmentstandard.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import me.langyue.equipmentstandard.api.data.ItemVerifier;

import java.lang.reflect.Type;

public class ItemVerifierDeserializer extends BaseDeserializer<ItemVerifier> {
    @Override
    public ItemVerifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonObject()) {
            // 兼容其他
            JsonElement id = getJsonElement(jsonElement, "id", "Item Verifier requires an id or an tag", "tag");
            if (id != null) {
                return new ItemVerifier(id.getAsString());
            }
            JsonElement tag = getJsonElement(jsonElement, "tag", "Item Verifier requires an id or an tag", "id");
            if (tag != null) {
                return new ItemVerifier("#" + tag.getAsString());
            }
        } else {
            return new ItemVerifier(jsonElement.getAsString());
        }
        throw new JsonParseException("Invalid value: " + jsonElement.getAsString());
    }
}
