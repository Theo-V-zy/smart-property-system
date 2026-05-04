package com.example.smartproperty.controller;

import com.example.smartproperty.config.AuthInterceptor;
import com.example.smartproperty.config.GlobalExceptionHandler;
import com.example.smartproperty.dto.OrderAssistantResponse;
import com.example.smartproperty.service.OrderAssistantService;
import com.example.smartproperty.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderAssistantService orderAssistantService;

    @MockBean
    private AuthInterceptor authInterceptor;

    @BeforeEach
    void setup() throws Exception {
        when(authInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void shouldSubmitOrderWhenRequestIsValid() throws Exception {
        doNothing().when(orderService).create(any());

        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer demo-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "category": "REPAIR",
                      "subtype": "水电维修",
                      "description": "厨房水槽漏水",
                      "address": "1栋1单元101",
                      "images": []
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("提交成功"));
    }

    @Test
    void shouldReturnValidationErrorWhenOrderDescriptionIsBlank() throws Exception {
        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer demo-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "category": "REPAIR",
                      "subtype": "水电维修",
                      "description": "",
                      "address": "1栋1单元101",
                      "images": []
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("请输入问题描述"));
    }

    @Test
    void shouldReturnAssistantResultWhenAssistEndpointIsCalled() throws Exception {
        OrderAssistantResponse response = new OrderAssistantResponse();
        response.setCategory("REPAIR");
        response.setSubtype("其他");
        response.setPolishedDescription("报修问题如下，烟雾报警器损坏。");
        response.setUrgency("LOW");
        response.setTips(List.of("建议补充具体位置。"));
        response.setUsedModel(false);
        response.setEngineName("本地规则助手");
        when(orderAssistantService.assist(any())).thenReturn(response);

        mockMvc.perform(post("/api/orders/assistant")
                .header("Authorization", "Bearer demo-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "text": "烟雾报警器损坏"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.subtype").value("其他"))
            .andExpect(jsonPath("$.data.engineName").value("本地规则助手"));
    }
}
