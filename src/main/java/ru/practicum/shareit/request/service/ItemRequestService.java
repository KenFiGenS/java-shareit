package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getRequestById(long userId, long requestId);

    List<ItemRequestDto> getRequestsByUser(long userId);
}
