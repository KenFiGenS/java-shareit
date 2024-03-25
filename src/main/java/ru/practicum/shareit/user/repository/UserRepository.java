package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    User patch(long id, User user);

    User getUserById(long id);

    List<User> getAllUsers();

    void removeUser(long id);
}
