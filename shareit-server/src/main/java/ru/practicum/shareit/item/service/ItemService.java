package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;

import java.util.List;

public interface ItemService {

    ItemDto createItem(long userId, ItemDto item);

    ItemDto updateItem(long userId, long id, ItemDto item);

    ItemDtoWithBookingAndComments getItemById(long userId, long id);

    List<ItemDtoWithBookingAndComments> getAllItemByOwner(long userId);

    List<ItemDto> search(String text);

    CommentDto createComment(long userId, long itemId, CommentDto commentDto);
}
