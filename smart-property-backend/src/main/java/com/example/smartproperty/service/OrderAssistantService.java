package com.example.smartproperty.service;

import com.example.smartproperty.dto.OrderAssistantRequest;
import com.example.smartproperty.dto.OrderAssistantResponse;

public interface OrderAssistantService {

    OrderAssistantResponse assist(OrderAssistantRequest request);
}
