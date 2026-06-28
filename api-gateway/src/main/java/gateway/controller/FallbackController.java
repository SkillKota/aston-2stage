package gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/user-service")
    public Mono<ResponseEntity<FallbackResponse>> userServiceFallback() {
        return fallback("user-service временно недоступен");
    }

    @GetMapping("/notification-service")
    public Mono<ResponseEntity<FallbackResponse>> notificationServiceFallback() {
        return fallback("notification-service временно недоступен");
    }

    private Mono<ResponseEntity<FallbackResponse>> fallback(String message) {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new FallbackResponse(message)));
    }

    public record FallbackResponse(String message) {
    }
}
