package com.ikeyit.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

public class JsonUtils {

    private final static ObjectMapper mapper = buildObjectMapper();

    private final static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    public static String writeValueAsString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Json serialization error", e);
        }
    }

    public static <T> T readValue(String content, Class<T> valueType) {
        try {
            return mapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Json deserialization error", e);
        }
    }

    public static <T> List<T> readValueAsList(String content, Class<T> valueType) {
        try {
            CollectionType javaType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, valueType);
            return mapper.readValue(content, javaType);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Json deserialization error", e);
        }
    }
}
