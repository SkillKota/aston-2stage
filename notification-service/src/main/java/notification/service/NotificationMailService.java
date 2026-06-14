package notification.service;

import notification.event.UserOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationMailService {
    private static final String CREATED_TEXT = "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан.";
    private static final String DELETED_TEXT = "Здравствуйте! Ваш аккаунт был удалён.";

    private final JavaMailSender mailSender;
    private final String from;

    public NotificationMailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from:no-reply@example.com}") String from
    ) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendUserOperationEmail(String email, UserOperation operation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Уведомление аккаунта");
        message.setText(messageText(operation));
        mailSender.send(message);
    }

    private String messageText(UserOperation operation) {
        return switch (operation) {
            case CREATED -> CREATED_TEXT;
            case DELETED -> DELETED_TEXT;
        };
    }
}
