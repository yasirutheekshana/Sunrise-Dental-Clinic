package com.sunrisedental.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sunrisedental.exception.ApplicationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility wrapper around Jackson ObjectMapper for clean, consistent JSON serialization.
 */
public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private JsonUtil() {
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static String toJson(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ApplicationException("Error serializing object to JSON: " + e.getMessage(), e, 500);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new ApplicationException("Error deserializing JSON: " + e.getMessage(), e, 400);
        }
    }

    public static <T> T fromJson(InputStream is, Class<T> clazz) {
        try {
            return MAPPER.readValue(is, clazz);
        } catch (IOException e) {
            throw new ApplicationException("Error deserializing JSON from stream: " + e.getMessage(), e, 400);
        }
    }

    public static <T> List<T> fromJsonList(File file, Class<T> elementType) {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try {
            JavaType listType = MAPPER.getTypeFactory().constructCollectionType(List.class, elementType);
            List<T> result = MAPPER.readValue(file, listType);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Warning: Corrupted or invalid JSON in " + file.getName() + ", resetting empty list: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static <T> void writeToFile(File file, List<T> list) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            MAPPER.writeValue(file, list);
        } catch (IOException e) {
            throw new ApplicationException("Failed to persist data to " + file.getName() + ": " + e.getMessage(), e, 500);
        }
    }
}
