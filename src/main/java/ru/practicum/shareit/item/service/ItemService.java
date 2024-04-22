package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {

    ItemDto createItem(long userId, ItemDto item);

    ItemDto updateItem(long userId, long id, ItemDto item);

    ItemDtoWithBooking getItemById(long userId, long id);

    List<ItemDtoWithBooking> getAllItemByOwner(long userId);

    List<ItemDto> search(String text);
}
