package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.exceptionControllers.Marker;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Builder
@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z][а-яА-ЯёЁa-zA-Z0-9]+$", message = "недопустимые символы в поле name")
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    private String description;
    @NotBlank(groups = Marker.OnCreate.class)
    @Pattern(regexp = "^(true|false)$", message = "значения переменной available должны быть: true или false")
    private boolean available;
    private ItemRequest request;
}
