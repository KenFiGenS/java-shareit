package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptionControllers.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z][а-яА-ЯёЁa-zA-Z0-9]+$", message = "недопустимые символы в поле name")
    private String name;
}
