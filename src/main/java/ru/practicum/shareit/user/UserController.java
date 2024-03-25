package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptionControllers.Marker;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public User createUser(@Valid @RequestBody User user) {
        log.info("Выполняется запрос на создание пользователя");
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable long id, @RequestBody User user) {
        log.info("Выполниется запрос обновления пользователя под ID: " + id);
        return userService.patch(id, user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable long id) {
        log.info("Выполняется запрос поиска пользователя по ID: " + id);
        return userService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUser() {
        log.info("Выполняется запрос поиска всех пользователей");
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable long id) {
        log.info("Выполняется запрос удаления пользователя");
        userService.removeUser(id);
    }
}
