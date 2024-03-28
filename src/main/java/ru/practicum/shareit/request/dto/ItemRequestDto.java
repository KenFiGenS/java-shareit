package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@RequiredArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private User requester;
    LocalDateTime created;
}
