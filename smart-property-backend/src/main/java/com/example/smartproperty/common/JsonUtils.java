package com.example.smartproperty.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public final class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    public static String toJson(List<String> values) {
        try {
            return OBJECT_MAPPER.writeValueAsString(values == null ? Collections.emptyList() : values);
        } catch (JsonProcessingException exception) {
            throw new BusinessException("图片数据保存失败");
        }
    }

    public static List<String> toList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException exception) {
            return Collections.emptyList();
        }
    }
}
