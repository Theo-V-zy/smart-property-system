package com.example.smartproperty.controller;

import com.example.smartproperty.common.ApiResponse;
import com.example.smartproperty.dto.LostFoundCreateRequest;
import com.example.smartproperty.service.LostFoundService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lost-found")
public class LostFoundController {

    private final LostFoundService lostFoundService;

    public LostFoundController(LostFoundService lostFoundService) {
        this.lostFoundService = lostFoundService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(@RequestParam(required = false) String type) {
        return ApiResponse.success(lostFoundService.list(type));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody LostFoundCreateRequest request) {
        lostFoundService.create(request);
        return ApiResponse.success("发布成功", null);
    }
}
