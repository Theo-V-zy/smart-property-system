package com.example.smartproperty.controller;

import com.example.smartproperty.common.ApiResponse;
import com.example.smartproperty.dto.NoticeCreateRequest;
import com.example.smartproperty.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        return ApiResponse.success(noticeService.list());
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody NoticeCreateRequest request) {
        noticeService.create(request);
        return ApiResponse.success("通知发布成功", null);
    }
}
