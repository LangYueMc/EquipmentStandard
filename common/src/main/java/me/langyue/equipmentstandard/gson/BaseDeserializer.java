package me.langyue.equipmentstandard.gson;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.Arrays;

public abstract class BaseDeserializer<T> implements JsonDeserializer<T> {

    protected JsonElement getJsonElement(JsonElement object, String key) {
        return getJsonElement(object, key, null);
    }

    protected JsonElement getJsonElement(JsonElement object, String key, String error, String... mutuallyExclusive) {
        JsonElement element = null;
        JsonObject jsonObject = object.getAsJsonObject();
        if (jsonObject.getAsJsonObject().has(key)) {
            element = jsonObject.get(key);
        }
        if (error != null && element == null && Arrays.stream(mutuallyExclusive).noneMatch(it -> jsonObject.get(it) != null)) {
            throw new JsonParseException(error);
        }
        return element;
    }
}
