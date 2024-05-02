package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.user.userDto.UserMapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto patch(long id, UserDto userDto) {
        UserDto userForUpdate = getUserById(id);
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) userForUpdate.setEmail(userDto.getEmail());
        if (userDto.getName() != null && !userDto.getName().isBlank()) userForUpdate.setName(userDto.getName());
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userForUpdate)));
    }

    @SneakyThrows
    @Override
    @Transactional
    public UserDto getUserById(long id) {
        User currentUser = userRepository.getReferenceById(id);
        return UserMapper.toUserDto(currentUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void removeUser(long id) {
        User currentUser = userRepository.getReferenceById(id);
        if (currentUser == null) {
            throw new DataNotFound("Пользователь c ID: " + id + " не найден!");
        }
        userRepository.deleteById(id);
    }
}
