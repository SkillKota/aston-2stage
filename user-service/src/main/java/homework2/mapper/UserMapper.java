package homework2.mapper;

import homework2.dto.UserResponseDto;
import homework2.entity.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserResponseDto toResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}
