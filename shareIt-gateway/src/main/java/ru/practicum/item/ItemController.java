package ru.practicum.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.exception.Marker;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.item.dto.ItemDtoWithBookingAndComments;


import java.util.List;

import static ru.practicum.constans.Constants.REQUEST_HEADER_NAME;


@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private ItemClient itemClient;


    @PostMapping
    public ItemDto createItem(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                              @Validated(Marker.OnCreate.class) @RequestBody ItemDto item) {
        log.info("Выполняется запрос создания вещи");
        return itemClient.create(userId, item);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                              @PathVariable long id,
                              @RequestBody ItemDto item) {
        log.info("Выполняется запрос обновления вещи под ID: {} пользователя под ID: {}", id, userId);
        return itemClient.updateItem(userId, id, item);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBookingAndComments getItemById(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                                     @PathVariable long itemId) {
        log.info("Выполняется запрос получения информации вещи по ID: {}, от пользователя: {}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoWithBookingAndComments> getAllItemByOwner(@RequestHeader(REQUEST_HEADER_NAME) long userId) {
        log.info("Выполняется запрос получения всех вещей пользователя под ID: {}", userId);
        return itemClient.getAllItemByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByNameAndDescription(@RequestParam String text) {
        log.info("Выполняется запрос поиска по имени и описанию. Текст запроса: {}", text);
        return itemClient.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                    @PathVariable long itemId,
                                    @Validated(Marker.OnCreate.class) @RequestBody CommentDto commentDto) {
        log.info("Выполняется запрос создания отзыва для вещи под ID: {}, от пользователя: {}", itemId, userId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
