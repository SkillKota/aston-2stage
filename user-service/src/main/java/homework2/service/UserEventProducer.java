package homework2.service;

import homework2.event.UserOperation;
import homework2.event.UserOperationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {
    private final KafkaTemplate<String, UserOperationEvent> kafkaTemplate;
    private final String topicName;

    public UserEventProducer(
            KafkaTemplate<String, UserOperationEvent> kafkaTemplate,
            @Value("${app.kafka.user-events-topic:user-events}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public void sendUserCreated(String email) {
        send(UserOperation.CREATED, email);
    }

    public void sendUserDeleted(String email) {
        send(UserOperation.DELETED, email);
    }

    private void send(UserOperation operation, String email) {
        kafkaTemplate.send(topicName, email, new UserOperationEvent(operation, email));
    }
}
