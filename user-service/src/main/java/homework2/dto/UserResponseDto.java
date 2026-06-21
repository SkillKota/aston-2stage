package homework2.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Пользователь")
public record UserResponseDto(
        @Schema(description = "Идентификатор пользователя", example = "1")
        Long id,
        @Schema(description = "Имя пользователя", example = "Иван")
        String name,
        @Schema(description = "Email пользователя", example = "ivan@example.com")
        String email,
        @Schema(description = "Возраст пользователя", example = "25")
        Integer age,
        @Schema(description = "Дата и время создания пользователя")
        LocalDateTime createdAt
) {
}
