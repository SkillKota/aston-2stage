package homework2.validator;

import homework2.dto.UserRequestDto;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    public void validate(UserRequestDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Данные пользователя не должны быть null");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не должно быть пустым");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("Email пользователя не должен быть пустым");
        }
        if (request.age() == null || request.age() <= 0) {
            throw new IllegalArgumentException("Возраст пользователя должен быть больше 0");
        }
    }
}
