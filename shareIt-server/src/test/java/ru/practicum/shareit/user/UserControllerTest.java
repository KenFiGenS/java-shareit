package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

        Mockito.when(userService.create(ArgumentMatchers.any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    userDto.setId(1L);
                    return userDto;
                });

        mockMvc.perform((MockMvcRequestBuilders.post("/users"))
                        .content(objectMapper.writeValueAsString(userDtoForSave))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1L), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDtoForSave.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDtoForSave.getName())));
    }

    @SneakyThrows
    @Test
    void updateUser() {
        UserDto userDtoForPatch = new UserDto(0, "updateEmail@mail.ru", "UpdateName");
        UserDto userAfterPatch = new UserDto(1, "updateEmail@mail.ru", "UpdateName");

        Mockito.when(userService.patch(ArgumentMatchers.anyLong(), ArgumentMatchers.any(UserDto.class)))
                .thenReturn(userAfterPatch);

        mockMvc.perform((MockMvcRequestBuilders.patch("/users/1"))
                        .content(objectMapper.writeValueAsString(userDtoForPatch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(userAfterPatch.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userAfterPatch.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userAfterPatch.getName())));
    }

    @SneakyThrows
    @Test
    void getUserById() {
        UserDto user = new UserDto(1, "updateEmail@mail.ru", "UpdateName");

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong()))
                .thenReturn(user);

        mockMvc.perform((MockMvcRequestBuilders.get("/users/1"))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1L), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(user.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(user.getName())));
    }

    @SneakyThrows
    @Test
    void getAllUser() {
        List<UserDto> users = List.of(
                new UserDto(1, "email@mail.ru", "NameUser"),
                new UserDto(2, "updateEmail@mail.ru", "UpdateName")
        );

        Mockito.when(userService.getAllUsers())
                .thenReturn(users);

        mockMvc.perform((MockMvcRequestBuilders.get("/users"))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @SneakyThrows
    @Test
    void removeUser() {
        mockMvc.perform((MockMvcRequestBuilders.delete("/users/1"))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}