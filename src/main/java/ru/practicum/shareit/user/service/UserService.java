package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userDto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto user);

    UserDto patch(long id, UserDto user);

    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    void removeUser(long id);
}
