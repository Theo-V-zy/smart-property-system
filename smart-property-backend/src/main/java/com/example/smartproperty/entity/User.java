package com.example.smartproperty.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String role;
    private String username;
    private String passwordHash;
    private String name;
    private String phone;
    private String communityName;
    private String address;
    private String businessScope;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
