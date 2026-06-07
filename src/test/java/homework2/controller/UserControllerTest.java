package homework2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import homework2.dto.UserRequestDto;
import homework2.entity.User;
import homework2.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUserShouldReturnCreatedUserDto() throws Exception {
        User user = user(1L, "Иван", "ivan@example.com", 25);
        when(userService.createUser("Иван", "ivan@example.com", 25)).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UserRequestDto("Иван", "ivan@example.com", 25))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void findUserByIdShouldReturnUserDto() throws Exception {
        when(userService.findUserById(1L)).thenReturn(Optional.of(user(1L, "Иван", "ivan@example.com", 25)));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ivan@example.com"));
    }

    @Test
    void findUserByIdShouldReturnNotFound() throws Exception {
        when(userService.findUserById(100L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllUsersShouldReturnUserDtos() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(
                user(1L, "Иван", "ivan@example.com", 25),
                user(2L, "Мария", "maria@example.com", 30)
        ));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value("ivan@example.com"))
                .andExpect(jsonPath("$[1].email").value("maria@example.com"));
    }

    @Test
    void updateUserShouldReturnUpdatedUserDto() throws Exception {
        User user = user(1L, "Петр", "petr@example.com", 31);
        when(userService.updateUser(1L, "Петр", "petr@example.com", 31)).thenReturn(Optional.of(user));

        mockMvc.perform(put("/api/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UserRequestDto("Петр", "petr@example.com", 31))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Петр"))
                .andExpect(jsonPath("$.email").value("petr@example.com"))
                .andExpect(jsonPath("$.age").value(31));
    }

    @Test
    void updateUserShouldReturnNotFound() throws Exception {
        when(userService.updateUser(100L, "Петр", "petr@example.com", 31)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/100")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UserRequestDto("Петр", "petr@example.com", 31))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserShouldReturnNoContent() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserShouldReturnNotFound() throws Exception {
        when(userService.deleteUser(100L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUserShouldRejectInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UserRequestDto("", "bad-email", 0))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Некорректные данные пользователя"));
    }

    @Test
    void createUserShouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        when(userService.createUser("Иван", "ivan@example.com", 25))
                .thenThrow(new DataIntegrityViolationException("duplicate email"));

        mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UserRequestDto("Иван", "ivan@example.com", 25))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email уже используется"));
    }

    private static User user(Long id, String name, String email, Integer age) {
        User user = new User(name, email, age);
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.of(2026, 6, 7, 10, 0));
        return user;
    }
}
