package notification.controller;

import jakarta.validation.Valid;
import notification.dto.EmailNotificationRequest;
import notification.service.NotificationMailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationMailService notificationMailService;

    public NotificationController(NotificationMailService notificationMailService) {
        this.notificationMailService = notificationMailService;
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailNotificationRequest request) {
        notificationMailService.sendUserOperationEmail(request.email(), request.operation());
        return ResponseEntity.accepted().build();
    }
}
