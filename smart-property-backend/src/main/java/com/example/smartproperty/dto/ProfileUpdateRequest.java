package com.example.smartproperty.dto;

import lombok.Data;

@Data
public class ProfileUpdateRequest {

    private String name;
    private String phone;
    private String address;
    private String businessScope;
    private String avatar;
}
