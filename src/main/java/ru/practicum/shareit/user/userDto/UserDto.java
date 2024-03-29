package ru.practicum.shareit.user.userDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import ru.practicum.shareit.exceptionControllers.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Getter
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Email(groups = Marker.OnCreate.class)
    @Email(groups = Marker.OnUpdate.class)
    private String email;
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
}
