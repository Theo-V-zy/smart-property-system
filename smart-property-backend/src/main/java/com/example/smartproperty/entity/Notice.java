package com.example.smartproperty.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notice {
    private Long id;
    private String title;
    private String content;
    private Long publisherId;
    private String publisherName;
    private Integer pinned;
    private LocalDateTime createdAt;
}
