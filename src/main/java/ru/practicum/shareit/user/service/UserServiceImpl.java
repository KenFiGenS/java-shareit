package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;

    @Override
    public User create(User user) {
        emailExist(0, user);
        return userRepository.create(user);
    }

    @Override
    public User patch(long id, User user) {
        getUserById(id);
        emailExist(id, user);
        return userRepository.patch(id, user);
    }

    @Override
    public User getUserById(long id) {
        User currentUser = userRepository.getUserById(id);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID: " + id + " не найден");
        }
        return currentUser;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public void removeUser(long id) {
        userRepository.removeUser(id);
    }

    private void emailExist(long id, User user) {
        userRepository.getAllUsers()
                .forEach(u -> { if (u.getEmail().equals(user.getEmail()) && u.getId() != id)
                    throw new IllegalArgumentException("Данный email уже существует");
                });
    }
}
