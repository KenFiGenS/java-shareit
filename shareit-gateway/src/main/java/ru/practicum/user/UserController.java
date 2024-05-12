package ru.practicum.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.Marker;
import ru.practicum.user.userDto.UserDto;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private UserClient userClient;

    @PostMapping
    public UserDto createUser(@Validated({Marker.OnCreate.class}) @RequestBody UserDto userDto) {
        log.info("Выполняется запрос на создание пользователя");
        return userClient.post(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable long id,
                              @Validated({Marker.OnUpdate.class}) @RequestBody UserDto userDto) {
        log.info("Выполняется запрос обновления пользователя под ID: {}", id);
        return userClient.patch(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.info("Выполняется запрос поиска пользователя по ID: {}", id);
        return userClient.get(id);
    }

    @GetMapping
    public List<UserDto> getAllUser() {
        log.info("Выполняется запрос поиска всех пользователей");
        return userClient.getAll();
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable long id) {
        log.info("Выполняется запрос удаления пользователя");
        userClient.delete(id);
    }
}
