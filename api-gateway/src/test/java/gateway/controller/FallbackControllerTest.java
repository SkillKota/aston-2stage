package gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(
        controllers = FallbackController.class,
        properties = "spring.cloud.config.enabled=false"
)
class FallbackControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void userServiceFallbackShouldReturnServiceUnavailable() {
        webTestClient.get()
                .uri("/fallback/user-service")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("user-service временно недоступен");
    }

    @Test
    void notificationServiceFallbackShouldReturnServiceUnavailable() {
        webTestClient.get()
                .uri("/fallback/notification-service")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("notification-service временно недоступен");
    }
}
