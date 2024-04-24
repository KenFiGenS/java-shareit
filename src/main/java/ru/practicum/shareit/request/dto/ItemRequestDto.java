package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private User requester;
    LocalDateTime created;
}
