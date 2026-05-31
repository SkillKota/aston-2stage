package homework2.service;

import homework2.dao.UserDao;
import homework2.entity.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = Objects.requireNonNull(userDao, "UserDao не должен быть null");
    }

    @Override
    public User createUser(String name, String email, Integer age) {
        validateUserData(name, email, age);
        return userDao.create(new User(name, email, age));
    }

    @Override
    public Optional<User> findUserById(Long id) {
        Objects.requireNonNull(id, "Id пользователя не должен быть null");
        return userDao.findById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userDao.findAll();
    }

    @Override
    public Optional<User> updateUser(Long id, String name, String email, Integer age) {
        Objects.requireNonNull(id, "Id пользователя не должен быть null");
        validateUserData(name, email, age);

        Optional<User> existingUser = userDao.findById(id);
        if (existingUser.isEmpty()) {
            return Optional.empty();
        }

        User user = existingUser.get();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return Optional.of(userDao.update(user));
    }

    @Override
    public boolean deleteUser(Long id) {
        Objects.requireNonNull(id, "Id пользователя не должен быть null");
        return userDao.deleteById(id);
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
