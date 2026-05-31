package homework2.service;

import homework2.dao.UserDao;
import homework2.entity.User;
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
    private UserDao userDao;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUserShouldPassNewUserToDao() {
        User savedUser = new User("Иван", "ivan@example.com", 25);
        when(userDao.create(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser("Иван", "ivan@example.com", 25);

        assertSame(savedUser, result);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).create(captor.capture());
        assertEquals("Иван", captor.getValue().getName());
        assertEquals("ivan@example.com", captor.getValue().getEmail());
        assertEquals(25, captor.getValue().getAge());
    }

    @Test
    void createUserShouldRejectInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("", "ivan@example.com", 25));
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("Иван", "", 25));
        assertThrows(IllegalArgumentException.class, () -> userService.createUser("Иван", "ivan@example.com", 0));

        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void findUserByIdShouldReturnDaoResult() {
        User user = new User("Мария", "maria@example.com", 30);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findUserById(1L);

        assertTrue(result.isPresent());
        assertSame(user, result.get());
        verify(userDao).findById(1L);
    }

    @Test
    void findAllUsersShouldReturnDaoResult() {
        List<User> users = List.of(
                new User("Иван", "ivan@example.com", 25),
                new User("Мария", "maria@example.com", 30)
        );
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertSame(users, result);
        verify(userDao).findAll();
    }

    @Test
    void updateUserShouldUpdateExistingUser() {
        User existingUser = new User("Старое имя", "old@example.com", 20);
        User updatedUser = new User("Новое имя", "new@example.com", 21);
        when(userDao.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userDao.update(existingUser)).thenReturn(updatedUser);

        Optional<User> result = userService.updateUser(1L, "Новое имя", "new@example.com", 21);

        assertTrue(result.isPresent());
        assertSame(updatedUser, result.get());
        assertEquals("Новое имя", existingUser.getName());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals(21, existingUser.getAge());
        verify(userDao).findById(1L);
        verify(userDao).update(existingUser);
    }

    @Test
    void updateUserShouldReturnEmptyWhenUserNotFound() {
        when(userDao.findById(100L)).thenReturn(Optional.empty());

        Optional<User> result = userService.updateUser(100L, "Имя", "user@example.com", 22);

        assertFalse(result.isPresent());
        verify(userDao).findById(100L);
        verify(userDao, never()).update(any(User.class));
    }

    @Test
    void deleteUserShouldReturnDaoResult() {
        when(userDao.deleteById(1L)).thenReturn(true);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userDao).deleteById(1L);
    }

    @Test
    void methodsShouldRejectNullArguments() {
        assertThrows(NullPointerException.class, () -> new UserServiceImpl(null));
        assertThrows(NullPointerException.class, () -> userService.findUserById(null));
        assertThrows(NullPointerException.class, () -> userService.updateUser(null, "Имя", "user@example.com", 22));
        assertThrows(NullPointerException.class, () -> userService.deleteUser(null));
    }
}
