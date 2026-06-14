package homework2.service;

import homework2.dto.UserRequestDto;
import homework2.dto.UserResponseDto;
import homework2.entity.User;
import homework2.mapper.UserMapper;
import homework2.repository.UserRepository;
import homework2.validator.UserValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserServiceImpl(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto request) {
        userValidator.validate(request);
        User user = new User(request.name(), request.email(), request.age());
        return UserMapper.toResponseDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> findUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toResponseDto)
                .toList();
    }

    @Override
    public Optional<UserResponseDto> updateUser(Long id, UserRequestDto request) {
        userValidator.validate(request);

        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            return Optional.empty();
        }

        User user = existingUser.get();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setAge(request.age());
        return Optional.of(UserMapper.toResponseDto(userRepository.save(user)));
    }

    @Override
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}
