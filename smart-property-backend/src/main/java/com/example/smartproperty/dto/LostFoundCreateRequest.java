package com.example.smartproperty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class LostFoundCreateRequest {

    @NotBlank(message = "请选择类型")
    private String type;

    @NotBlank(message = "请输入标题")
    private String title;

    @NotBlank(message = "请输入描述")
    private String description;

    @NotBlank(message = "请输入地点")
    private String pickupLocation;

    @NotBlank(message = "请输入联系人")
    private String contactName;

    @NotBlank(message = "请输入联系电话")
    private String contactPhone;

    private List<String> images;
}
