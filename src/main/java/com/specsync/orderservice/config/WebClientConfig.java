package com.specsync.orderservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.user-service.url}")
    private String userServiceUrl;

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    @Bean("userWebClient")
    public WebClient userWebClient() {
        return WebClient.builder()
                .baseUrl(userServiceUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    @Bean("productWebClient")
    public WebClient productWebClient() {
        return WebClient.builder()
                .baseUrl(productServiceUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
