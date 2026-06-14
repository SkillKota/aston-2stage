package notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import notification.event.UserOperation;

public record EmailNotificationRequest(
        @NotBlank @Email String email,
        @NotNull UserOperation operation
) {
}
