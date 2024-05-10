package ru.practicum.user.userDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.exception.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Email(groups = Marker.OnCreate.class)
    private String email;
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
}
