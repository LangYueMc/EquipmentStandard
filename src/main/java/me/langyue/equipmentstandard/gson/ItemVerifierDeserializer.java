package me.langyue.equipmentstandard.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import me.langyue.equipmentstandard.data.ItemVerifier;

import java.lang.reflect.Type;

public class ItemVerifierDeserializer extends BaseDeserializer<ItemVerifier> {
    @Override
    public ItemVerifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonElement id = getJsonElement(jsonElement, "id", "Item Verifier requires an id or an tag", "tag");
        JsonElement tag = getJsonElement(jsonElement, "tag", "Item Verifier requires an id or an tag", "id");
        return new ItemVerifier(
                id == null ? "" : id.getAsString(),
                tag == null ? "" : tag.getAsString()
        );
    }
}
