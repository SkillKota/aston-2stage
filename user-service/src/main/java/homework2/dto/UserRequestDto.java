package homework2.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Данные для создания или обновления пользователя")
public record UserRequestDto(
        @Schema(description = "Имя пользователя", example = "Иван")
        @NotBlank String name,
        @Schema(description = "Email пользователя", example = "ivan@example.com")
        @NotBlank @Email String email,
        @Schema(description = "Возраст пользователя", example = "25")
        @NotNull @Positive Integer age
) {
}
