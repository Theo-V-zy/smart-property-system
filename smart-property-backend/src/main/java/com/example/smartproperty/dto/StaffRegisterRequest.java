package com.example.smartproperty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StaffRegisterRequest {

    @NotBlank(message = "请输入账号")
    private String username;

    @NotBlank(message = "请输入密码")
    private String password;

    @NotBlank(message = "请输入姓名")
    private String name;

    @NotBlank(message = "请输入手机号")
    private String phone;

    @NotBlank(message = "请输入负责业务")
    private String businessScope;
}
