package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    User create(User user);

    User patch(long id, User user);

    User getUserById(long id);

    List<User> getAllUsers();

    void removeUser(long id);
}
