package com.example.smartproperty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OwnerRegisterRequest {

    @NotBlank(message = "请输入账号")
    private String username;

    @NotBlank(message = "请输入密码")
    private String password;

    @NotBlank(message = "请输入姓名")
    private String name;

    @NotBlank(message = "请输入手机号")
    private String phone;

    @NotBlank(message = "请输入小区名称")
    private String communityName;

    @NotBlank(message = "请输入楼栋房号")
    private String address;
}
