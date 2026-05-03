package com.example.smartproperty.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceOrder {
    private Long id;
    private String orderNo;
    private Long ownerId;
    private String ownerName;
    private String phone;
    private String category;
    private String subtype;
    private String description;
    private String imagesJson;
    private String address;
    private String status;
    private String reply;
    private Long handlerId;
    private String handlerName;
    private String handleRecord;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
