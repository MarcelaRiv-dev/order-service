package com.specsync.orderservice.client;

import com.specsync.orderservice.dto.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Slf4j
public class ProductServiceClient {

    private final WebClient productServiceClient;

    public ProductServiceClient(@Qualifier("productServiceClient") WebClient productServiceClient) {
        this.productServiceClient = productServiceClient;
    }

    public Optional<ProductResponse> getProductById(Long id) {
        try {
            ProductResponse product = productServiceClient
                    .get()
                    .uri("/api/products/{id}", id)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.warn("Product not found or client error for productId={}, status={}", id, clientResponse.statusCode());
                        return Mono.empty();
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        log.error("Server error from product-service for productId={}, status={}", id, clientResponse.statusCode());
                        return Mono.error(new RuntimeException("product-service is unavailable"));
                    })
                    .bodyToMono(ProductResponse.class)
                    .block();
            return Optional.ofNullable(product);
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Product with id={} not found in product-service", id);
            return Optional.empty();
        } catch (WebClientResponseException e) {
            log.error("Error calling product-service for productId={}: {} {}", id, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to retrieve product from product-service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error calling product-service for productId={}: {}", id, e.getMessage());
            throw new RuntimeException("product-service is unavailable: " + e.getMessage(), e);
        }
    }
}
