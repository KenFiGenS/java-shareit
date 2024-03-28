package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto createItem(long userId, Item item);

    ItemDto updateItem(long userId, long id, Item item);

    ItemDto getItemById(long userId, long id);

    List<ItemDto> getAllItemByOwner(long userId);

    List<ItemDto> search(String text);
}
