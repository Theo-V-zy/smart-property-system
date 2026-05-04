package com.example.smartproperty.controller;

import com.example.smartproperty.common.ApiResponse;
import com.example.smartproperty.dto.OrderAssistantRequest;
import com.example.smartproperty.dto.OrderAssistantResponse;
import com.example.smartproperty.dto.EvaluationCreateRequest;
import com.example.smartproperty.dto.OrderCreateRequest;
import com.example.smartproperty.dto.OrderProcessRequest;
import com.example.smartproperty.service.OrderAssistantService;
import com.example.smartproperty.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderAssistantService orderAssistantService;

    public OrderController(OrderService orderService, OrderAssistantService orderAssistantService) {
        this.orderService = orderService;
        this.orderAssistantService = orderAssistantService;
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody OrderCreateRequest request) {
        orderService.create(request);
        return ApiResponse.success("提交成功", null);
    }

    @PostMapping("/assistant")
    public ApiResponse<OrderAssistantResponse> assist(@Valid @RequestBody OrderAssistantRequest request) {
        return ApiResponse.success(orderAssistantService.assist(request));
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String category) {
        return ApiResponse.success(orderService.list(status, category));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        return ApiResponse.success(orderService.detail(id));
    }

    @PutMapping("/{id}/process")
    public ApiResponse<Void> process(@PathVariable Long id, @Valid @RequestBody OrderProcessRequest request) {
        orderService.process(id, request);
        return ApiResponse.success("处理记录已更新", null);
    }

    @PostMapping("/{id}/evaluate")
    public ApiResponse<Void> evaluate(@PathVariable Long id, @Valid @RequestBody EvaluationCreateRequest request) {
        orderService.evaluate(id, request);
        return ApiResponse.success("评价成功", null);
    }
}
