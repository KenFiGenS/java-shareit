package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userDto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(User user);

    UserDto patch(long id, User user);

    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    void removeUser(long id);
}
