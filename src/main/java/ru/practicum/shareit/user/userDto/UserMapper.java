package ru.practicum.shareit.user.userDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class UserMapper {
    public static UserDto touserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}
