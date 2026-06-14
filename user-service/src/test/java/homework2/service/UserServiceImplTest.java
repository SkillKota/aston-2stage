package homework2.service;

import homework2.dto.UserRequestDto;
import homework2.dto.UserResponseDto;
import homework2.entity.User;
import homework2.repository.UserRepository;
import homework2.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserEventProducer userEventProducer;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userValidator, userEventProducer);
    }

    @Test
    void createUserShouldPassNewUserToRepository() {
        UserRequestDto request = new UserRequestDto("Иван", "ivan@example.com", 25);
        User savedUser = user(1L, "Иван", "ivan@example.com", 25);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto result = userService.createUser(request);

        assertEquals(1L, result.id());
        assertEquals("Иван", result.name());
        assertEquals("ivan@example.com", result.email());
        assertEquals(25, result.age());
        verify(userValidator).validate(request);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("Иван", captor.getValue().getName());
        assertEquals("ivan@example.com", captor.getValue().getEmail());
        assertEquals(25, captor.getValue().getAge());
        verify(userEventProducer).sendUserCreated("ivan@example.com");
    }

    @Test
    void createUserShouldRejectInvalidData() {
        UserValidator validator = new UserValidator();
        UserServiceImpl service = new UserServiceImpl(userRepository, validator, userEventProducer);

        assertThrows(IllegalArgumentException.class,
                () -> service.createUser(new UserRequestDto("", "ivan@example.com", 25)));
        assertThrows(IllegalArgumentException.class,
                () -> service.createUser(new UserRequestDto("Иван", "", 25)));
        assertThrows(IllegalArgumentException.class,
                () -> service.createUser(new UserRequestDto("Иван", "ivan@example.com", 0)));
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(userEventProducer);
    }

    @Test
    void findUserByIdShouldReturnRepositoryResult() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "Мария", "maria@example.com", 30)));

        Optional<UserResponseDto> result = userService.findUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals("maria@example.com", result.get().email());
        verify(userRepository).findById(1L);
        verifyNoInteractions(userValidator);
    }

    @Test
    void findAllUsersShouldReturnRepositoryResult() {
        List<User> users = List.of(
                user(1L, "Иван", "ivan@example.com", 25),
                user(2L, "Мария", "maria@example.com", 30)
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponseDto> result = userService.findAllUsers();

        assertEquals(2, result.size());
        assertEquals("ivan@example.com", result.get(0).email());
        assertEquals("maria@example.com", result.get(1).email());
        verify(userRepository).findAll();
        verifyNoInteractions(userValidator);
    }

    @Test
    void updateUserShouldUpdateExistingUser() {
        UserRequestDto request = new UserRequestDto("Новое имя", "new@example.com", 21);
        User existingUser = user(1L, "Старое имя", "old@example.com", 20);
        User updatedUser = user(1L, "Новое имя", "new@example.com", 21);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);

        Optional<UserResponseDto> result = userService.updateUser(1L, request);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals("Новое имя", result.get().name());
        assertEquals("new@example.com", result.get().email());
        verify(userValidator).validate(request);
        assertEquals("Новое имя", existingUser.getName());
        assertEquals("new@example.com", existingUser.getEmail());
        assertEquals(21, existingUser.getAge());
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUserShouldReturnEmptyWhenUserNotFound() {
        UserRequestDto request = new UserRequestDto("Имя", "user@example.com", 22);
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        Optional<UserResponseDto> result = userService.updateUser(100L, request);

        assertFalse(result.isPresent());
        verify(userValidator).validate(request);
        verify(userRepository).findById(100L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserShouldDeleteExistingUser() {
        User user = user(1L, "Иван", "ivan@example.com", 25);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
        verify(userEventProducer).sendUserDeleted("ivan@example.com");
    }

    @Test
    void deleteUserShouldReturnFalseWhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        boolean result = userService.deleteUser(100L);

        assertFalse(result);
        verify(userRepository).findById(100L);
        verify(userRepository, never()).delete(any(User.class));
        verifyNoInteractions(userEventProducer);
    }

    @Test
    void methodsShouldRejectNullArguments() {
        UserValidator validator = new UserValidator();

        assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
    }

    private static User user(Long id, String name, String email, Integer age) {
        User user = new User(name, email, age);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.of(2026, 6, 7, 10, 0));
        return user;
    }
}
