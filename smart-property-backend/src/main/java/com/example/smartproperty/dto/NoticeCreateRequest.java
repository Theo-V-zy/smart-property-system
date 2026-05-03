package com.example.smartproperty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoticeCreateRequest {

    @NotBlank(message = "请输入通知标题")
    private String title;

    @NotBlank(message = "请输入通知内容")
    private String content;
}
