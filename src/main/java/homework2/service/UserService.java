package homework2.service;

import homework2.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(String name, String email, Integer age);

    Optional<User> findUserById(Long id);

    List<User> findAllUsers();

    Optional<User> updateUser(Long id, String name, String email, Integer age);

    boolean deleteUser(Long id);
}
