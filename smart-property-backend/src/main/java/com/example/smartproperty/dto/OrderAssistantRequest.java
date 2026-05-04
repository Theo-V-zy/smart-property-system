package com.example.smartproperty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderAssistantRequest {

    @NotBlank(message = "请输入报修描述")
    private String text;
}
