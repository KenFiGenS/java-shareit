package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.exception.Marker;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.model.User;

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
