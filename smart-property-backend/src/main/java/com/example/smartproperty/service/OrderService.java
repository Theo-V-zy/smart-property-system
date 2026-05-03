package com.example.smartproperty.service;

import com.example.smartproperty.dto.EvaluationCreateRequest;
import com.example.smartproperty.dto.OrderCreateRequest;
import com.example.smartproperty.dto.OrderProcessRequest;

import java.util.List;
import java.util.Map;

public interface OrderService {
    void create(OrderCreateRequest request);

    List<Map<String, Object>> list(String status, String category);

    Map<String, Object> detail(Long id);

    void process(Long id, OrderProcessRequest request);

    void evaluate(Long id, EvaluationCreateRequest request);
}
