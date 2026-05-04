package com.example.smartproperty.service.impl;

import com.example.smartproperty.config.AiAssistantProperties;
import com.example.smartproperty.dto.OrderAssistantRequest;
import com.example.smartproperty.dto.OrderAssistantResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderAssistantServiceImplTest {

    @Test
    void shouldDetectRepairOrderWithFallbackRules() {
        AiAssistantProperties properties = new AiAssistantProperties();
        properties.setApiKey("");
        OrderAssistantServiceImpl service = new OrderAssistantServiceImpl(properties, new ObjectMapper());

        OrderAssistantRequest request = new OrderAssistantRequest();
        request.setText("我家厨房水槽下面一直漏水，地上已经有积水了，麻烦尽快处理");

        OrderAssistantResponse response = service.assist(request);

        assertEquals("REPAIR", response.getCategory());
        assertEquals("水电维修", response.getSubtype());
        assertEquals("HIGH", response.getUrgency());
        assertFalse(response.getUsedModel());
        assertTrue(response.getPolishedDescription().contains("漏水"));
    }
}
