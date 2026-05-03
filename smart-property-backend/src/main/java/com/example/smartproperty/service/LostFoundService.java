package com.example.smartproperty.service;

import com.example.smartproperty.dto.LostFoundCreateRequest;

import java.util.List;
import java.util.Map;

public interface LostFoundService {
    List<Map<String, Object>> list(String type);

    void create(LostFoundCreateRequest request);
}
