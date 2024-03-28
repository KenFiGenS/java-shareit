package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptionControllers.Marker;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * TODO Sprint add-controllers.
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    private String description;
    @NotBlank(groups = Marker.OnCreate.class)
    @Pattern(regexp = "^(true|false)$", message = "значения переменной available должны быть: true или false")
    private String available;
    private User owner;
    private ItemRequest request;
}
