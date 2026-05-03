package com.example.smartproperty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {

    @NotBlank(message = "请选择业务类型")
    private String category;

    @NotBlank(message = "请选择问题类型")
    private String subtype;

    @NotBlank(message = "请输入问题描述")
    private String description;

    private List<String> images;

    @NotBlank(message = "请输入地址")
    private String address;
}
