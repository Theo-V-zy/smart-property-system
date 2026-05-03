package com.example.smartproperty.service;

import com.example.smartproperty.dto.NoticeCreateRequest;

import java.util.List;
import java.util.Map;

public interface NoticeService {
    List<Map<String, Object>> list();

    void create(NoticeCreateRequest request);
}
