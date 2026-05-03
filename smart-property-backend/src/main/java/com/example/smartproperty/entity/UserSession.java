package com.example.smartproperty.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSession {
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
