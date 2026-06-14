package homework2.service;

import homework2.dto.UserRequestDto;
import homework2.dto.UserResponseDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDto createUser(UserRequestDto request);

    Optional<UserResponseDto> findUserById(Long id);

    List<UserResponseDto> findAllUsers();

    Optional<UserResponseDto> updateUser(Long id, UserRequestDto request);

    boolean deleteUser(Long id);
}
