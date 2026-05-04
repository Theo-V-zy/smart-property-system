package com.example.smartproperty.controller;

import com.example.smartproperty.config.AuthInterceptor;
import com.example.smartproperty.config.GlobalExceptionHandler;
import com.example.smartproperty.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthInterceptor authInterceptor;

    @Test
    void shouldReturnTokenWhenLoginRequestIsValid() throws Exception {
        when(authService.login(org.mockito.ArgumentMatchers.any())).thenReturn(Map.of(
            "token", "demo-token",
            "user", Map.of("name", "张三", "role", "OWNER")
        ));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "role": "OWNER",
                      "username": "owner01",
                      "password": "123456"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.token").value("demo-token"))
            .andExpect(jsonPath("$.data.user.name").value("张三"));
    }

    @Test
    void shouldReturnValidationErrorWhenPasswordIsBlank() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "role": "OWNER",
                      "username": "owner01",
                      "password": ""
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("请输入密码"));
    }
}
