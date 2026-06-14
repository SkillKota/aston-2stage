package notification.kafka;

import notification.event.UserOperationEvent;
import notification.service.NotificationMailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserOperationListener {
    private final NotificationMailService notificationMailService;

    public UserOperationListener(NotificationMailService notificationMailService) {
        this.notificationMailService = notificationMailService;
    }

    @KafkaListener(topics = "${app.kafka.user-events-topic:user-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserOperation(UserOperationEvent event) {
        notificationMailService.sendUserOperationEmail(event.email(), event.operation());
    }
}
