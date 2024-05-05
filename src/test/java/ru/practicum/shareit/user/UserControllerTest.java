package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void createUser() {
        UserDto userDtoForSave = new UserDto(0, "email@mail.ru", "NameUser");

        when(userService.create(any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    userDto.setId(1L);
                    return userDto;
                });

        mockMvc.perform((post("/users"))
                        .content(objectMapper.writeValueAsString(userDtoForSave))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.email", is(userDtoForSave.getEmail())))
                .andExpect(jsonPath("$.name", is(userDtoForSave.getName())));
    }

    @SneakyThrows
    @Test
    void updateUser() {
        UserDto userDtoForPatch = new UserDto(0, "updateEmail@mail.ru", "UpdateName");
        UserDto userAfterPatch = new UserDto(1, "updateEmail@mail.ru", "UpdateName");

        when(userService.patch(anyLong(), any(UserDto.class)))
                .thenReturn(userAfterPatch);

        mockMvc.perform((patch("/users/1"))
                        .content(objectMapper.writeValueAsString(userDtoForPatch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userAfterPatch.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(userAfterPatch.getEmail())))
                .andExpect(jsonPath("$.name", is(userAfterPatch.getName())));
    }

    @SneakyThrows
    @Test
    void getUserById() {
        UserDto user = new UserDto(1, "updateEmail@mail.ru", "UpdateName");

        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        mockMvc.perform((get("/users/1"))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.name", is(user.getName())));
    }

    @SneakyThrows
    @Test
    void getAllUser() {
        List<UserDto> users = List.of(
                new UserDto(1, "email@mail.ru", "NameUser"),
                new UserDto(2, "updateEmail@mail.ru", "UpdateName")
        );

        when(userService.getAllUsers())
                .thenReturn(users);

        mockMvc.perform((get("/users"))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @SneakyThrows
    @Test
    void removeUser() {
        mockMvc.perform((delete("/users/1"))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}