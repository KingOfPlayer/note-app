package com.note_app.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Value("${services.user-service}")
    private String userServiceUrl;

    @Value("${services.note-service}")
    private String noteServiceUrl;

    @Value("${services.file-service}")
    private String fileServiceUrl;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Bean
    public ServiceRegistry serviceRegistry() {
        ServiceRegistry registry = new ServiceRegistry();
        registry.register("/api/users/", userServiceUrl);
        registry.register("/api/auth/", userServiceUrl);
        registry.register("/api/notes/", noteServiceUrl);
        registry.register("/api/categories/", noteServiceUrl);
        registry.register("/api/files/", fileServiceUrl);
        return registry;
    }
}
