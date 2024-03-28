package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptionControllers.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Email
    private String email;
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
}
