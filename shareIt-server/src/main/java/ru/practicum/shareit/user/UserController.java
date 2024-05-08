package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated({Marker.OnCreate.class}) @RequestBody UserDto userDto) {
        log.info("Выполняется запрос на создание пользователя");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable long id,
                              @Validated({Marker.OnUpdate.class}) @RequestBody UserDto userDto) {
        log.info("Выполняется запрос обновления пользователя под ID: {}", id);
        return userService.patch(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.info("Выполняется запрос поиска пользователя по ID: {}", id);
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getAllUser() {
        log.info("Выполняется запрос поиска всех пользователей");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable long id) {
        log.info("Выполняется запрос удаления пользователя");
        userService.removeUser(id);
    }
}
