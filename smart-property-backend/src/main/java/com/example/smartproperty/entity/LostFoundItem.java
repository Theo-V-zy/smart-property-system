package com.example.smartproperty.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LostFoundItem {
    private Long id;
    private String type;
    private String title;
    private String description;
    private String pickupLocation;
    private String contactName;
    private String contactPhone;
    private String imagesJson;
    private String status;
    private Long publisherId;
    private String publisherName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
