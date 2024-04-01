package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(long uerId, Item item);

    Item updateItem(long userId, long id, Item item);

    Item getItemById(long userId, long id);

    List<Item> getAllItemByUser(long userId);

    List<Item> search(String text);
}
