package com.specsync.orderservice.client;

import com.specsync.orderservice.dto.UserResponse;
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
public class UserServiceClient {

    private final WebClient userServiceClient;

    public UserServiceClient(@Qualifier("userServiceClient") WebClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public Optional<UserResponse> getUserById(Long id) {
        try {
            UserResponse user = userServiceClient
                    .get()
                    .uri("/api/users/{id}", id)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.warn("User not found or client error for userId={}, status={}", id, clientResponse.statusCode());
                        return Mono.empty();
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        log.error("Server error from user-service for userId={}, status={}", id, clientResponse.statusCode());
                        return Mono.error(new RuntimeException("user-service is unavailable"));
                    })
                    .bodyToMono(UserResponse.class)
                    .block();
            return Optional.ofNullable(user);
        } catch (WebClientResponseException.NotFound e) {
            log.warn("User with id={} not found in user-service", id);
            return Optional.empty();
        } catch (WebClientResponseException e) {
            log.error("Error calling user-service for userId={}: {} {}", id, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to retrieve user from user-service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error calling user-service for userId={}: {}", id, e.getMessage());
            throw new RuntimeException("user-service is unavailable: " + e.getMessage(), e);
        }
    }
}
