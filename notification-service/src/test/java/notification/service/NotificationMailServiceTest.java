package notification.service;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;
import notification.event.UserOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class NotificationMailServiceTest {
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(3025, null, "smtp"));

    @Autowired
    private NotificationMailService notificationMailService;

    @BeforeEach
    void setUp() throws Exception {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void sendUserOperationEmailShouldSendCreatedMessage() throws Exception {
        notificationMailService.sendUserOperationEmail("ivan@example.com", UserOperation.CREATED);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals("ivan@example.com", messages[0].getAllRecipients()[0].toString());
        assertTrue(messages[0].getContent().toString()
                .contains("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."));
    }

    @Test
    void sendUserOperationEmailShouldSendDeletedMessage() throws Exception {
        notificationMailService.sendUserOperationEmail("ivan@example.com", UserOperation.DELETED);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals("ivan@example.com", messages[0].getAllRecipients()[0].toString());
        assertTrue(messages[0].getContent().toString().contains("Здравствуйте! Ваш аккаунт был удалён."));
    }
}
