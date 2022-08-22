package com.pawelwuuu.jsonUtil;

import com.google.gson.Gson;

/**
 * @author pawelwuuu
 * JsonManager provides static methods that serialize object to String or deserialize object into the specified class
 */
public class JsonManager {
    /**
     * Deserializes string which contains JSON format into the specified class.
     * @param parsedJson String which contains JSON format.
     * @param clazz Class that will be created.
     * @return Object of the specified class.
     */
    public static <U> U objectDeserialization(String parsedJson, Class<U> clazz){
        U u = new Gson().fromJson(parsedJson, clazz);

        return u;
    }
    /**
     * Serializes certain object and creates String of JSON format.
     * @param t Object which will be serialized.
     * @return String with parsed JSON.
     */
    public static <T> String objectSerialization(T t){
        return new Gson().toJson(t);
    }
}