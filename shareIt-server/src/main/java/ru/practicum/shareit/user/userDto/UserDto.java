package ru.practicum.shareit.user.userDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.exception.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Getter
@AllArgsConstructor
@Setter
public class UserDto {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Email(groups = Marker.OnCreate.class)
    private String email;
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
}