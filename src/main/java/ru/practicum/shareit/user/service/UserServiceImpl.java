package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptionControllers.DataNotFound;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.user.userDto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        emailExist(0, userDto);
        return UserMapper.touserDto(userRepository.create(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto patch(long id, UserDto userDto) {
        getUserById(id);
        emailExist(id, userDto);
        return UserMapper.touserDto(userRepository.patch(id, UserMapper.toUser(userDto)));
    }

    @SneakyThrows
    @Override
    public UserDto getUserById(long id) {
        User currentUser = userRepository.getUserById(id);
        if (currentUser == null) {
            throw new DataNotFound("Пользователь c ID: " + id + " не найден!");
        }
        return UserMapper.touserDto(currentUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(UserMapper::touserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeUser(long id) {
        userRepository.removeUser(id);
    }

    private void emailExist(long id, UserDto user) {
        userRepository.getAllUsers()
                .forEach(u -> {
                    if (u.getEmail().equals(user.getEmail()) && u.getId() != id)
                        throw new IllegalArgumentException("Данный email уже существует");
                });
    }
}
