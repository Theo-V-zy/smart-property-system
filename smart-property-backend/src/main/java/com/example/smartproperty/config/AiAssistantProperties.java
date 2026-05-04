package com.example.smartproperty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.ai")
public class AiAssistantProperties {

    private String baseUrl = "https://api.openai.com/v1";

    private String apiKey;

    private String model = "gpt-5.4-mini";

    private Integer timeoutMillis = 12000;
}
