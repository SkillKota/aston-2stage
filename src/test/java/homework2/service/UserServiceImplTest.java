package homework2.service;

import homework2.entity.User;
import homework2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserShouldPassNewUserToRepository() {
        User savedUser = new User("Иван", "ivan@example.com", 25);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser("Иван", "ivan@example.com", 25);

        assertSame(savedUser, result);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("Иван", captor.getValue().getName());
        assertEquals("ivan@example.com", captor.getValue().getEmail());
        assertEquals(25, captor.getValue().getAge());
    }

    @Test
    void createUserShouldRejectInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("", "ivan@example.com", 25));
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("Иван", "", 25));
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("Иван", "ivan@example.com", 0));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findUserByIdShouldReturnRepositoryResult() {
        User user = new User("Мария", "maria@example.com", 30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserById(1L);

        assertTrue(result.isPresent());
        assertSame(user, result.get());
        verify(userRepository).findById(1L);
    }

    @Test
    void findAllUsersShouldReturnRepositoryResult() {
        List<User> users = List.of(
                new User("Иван", "ivan@example.com", 25),
                new User("Мария", "maria@example.com", 30)
        );
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertSame(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void updateUserShouldUpdateExistingUser() {
        User existingUser = new User("Старое имя", "old@example.com", 20);
        User updatedUser = new User("Новое имя", "new@example.com", 21);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        Optional<User> result = userService.updateUser(1L, "Новое имя", "new@example.com", 21);

        assertTrue(result.isPresent());
        assertSame(updatedUser, result.get());
        assertEquals("Новое имя", existingUser.getName());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals(21, existingUser.getAge());
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUserShouldReturnEmptyWhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUser(100L, "Имя", "user@example.com", 22);

        assertFalse(result.isPresent());
        verify(userRepository).findById(100L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserShouldDeleteExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserShouldReturnFalseWhenUserNotFound() {
        when(userRepository.existsById(100L)).thenReturn(false);

        boolean result = userService.deleteUser(100L);

        assertFalse(result);
        verify(userRepository).existsById(100L);
        verify(userRepository, never()).deleteById(100L);
    }

    @Test
    void methodsShouldRejectNullArguments() {
        assertThrows(NullPointerException.class, () -> new UserServiceImpl(null));
        assertThrows(NullPointerException.class, () -> userService.findUserById(null));
        assertThrows(NullPointerException.class, () -> userService.updateUser(null, "Имя", "user@example.com", 22));
        assertThrows(NullPointerException.class, () -> userService.deleteUser(null));
    }
}
