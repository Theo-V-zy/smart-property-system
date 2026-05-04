package com.example.smartproperty.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderAssistantResponse {

    private String category;

    private String subtype;

    private String polishedDescription;

    private String urgency;

    private List<String> tips;

    private Boolean usedModel;

    private String engineName;

    private String engineMode;

    private String engineMessage;
}
