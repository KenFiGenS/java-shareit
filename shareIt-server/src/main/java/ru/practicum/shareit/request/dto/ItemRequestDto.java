package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.exception.Marker;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    private String description;
    private User requester;
    LocalDateTime created;
    List<ItemDto> items;
}
