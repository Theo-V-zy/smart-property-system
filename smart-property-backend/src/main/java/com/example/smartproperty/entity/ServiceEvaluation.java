package com.example.smartproperty.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceEvaluation {
    private Long id;
    private Long orderId;
    private Long ownerId;
    private Integer serviceScore;
    private Integer qualityScore;
    private Integer speedScore;
    private String content;
    private String imagesJson;
    private LocalDateTime createdAt;
}
