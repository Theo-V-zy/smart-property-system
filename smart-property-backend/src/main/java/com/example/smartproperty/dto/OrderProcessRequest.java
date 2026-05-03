package com.example.smartproperty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderProcessRequest {

    @NotBlank(message = "请选择处理状态")
    private String status;

    @NotBlank(message = "请填写处理记录")
    private String handleRecord;

    private String reply;
}
