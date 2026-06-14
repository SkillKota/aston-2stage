package notification.event;

public record UserOperationEvent(UserOperation operation, String email) {
}
