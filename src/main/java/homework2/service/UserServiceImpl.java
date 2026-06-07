package homework2.service;

import homework2.entity.User;
import homework2.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository не должен быть null");
    }

    @Override
    public User createUser(String name, String email, Integer age) {
        validateUserData(name, email, age);
        return userRepository.save(new User(name, email, age));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long id) {
        Objects.requireNonNull(id, "Id пользователя не должен быть null");
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> updateUser(Long id, String name, String email, Integer age) {
        Objects.requireNonNull(id, "Id пользователя не должен быть null");
        validateUserData(name, email, age);

        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            return Optional.empty();
        }

        User user = existingUser.get();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return Optional.of(userRepository.save(user));
    }

    @Override
    public boolean deleteUser(Long id) {
        Objects.requireNonNull(id, "Id пользователя не должен быть null");
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    private void validateUserData(String name, String email, Integer age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не должно быть пустым");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email пользователя не должен быть пустым");
        }
        if (age == null || age <= 0) {
            throw new IllegalArgumentException("Возраст пользователя должен быть больше 0");
        }
    }
}
