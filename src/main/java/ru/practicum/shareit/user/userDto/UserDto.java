package ru.practicum.shareit.user.userDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    private String email;
    private String name;
}
