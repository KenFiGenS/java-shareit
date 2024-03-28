package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    //private long requestId;
}
