package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptionControllers.Marker;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody Item item) {
        log.info("Выполняется запрос создания вещи");
        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long id,
                              @RequestBody Item item) {
        log.info("Выполняется запрос обновления вещи под ID: " + id + " пользователя под ID: " + userId);
        return itemService.updateItem(userId, id, item);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id) {
        log.info("Выполняется запрос получения информации вещи по ID: " + id + " от пользователя под ID: " + userId);
        return itemService.getItemById(userId, id);
    }

    @GetMapping
    public List<ItemDto> getAllItemByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Выполняется запрос получения всех вещей пользователя под ID: " + userId);
        return itemService.getAllItemByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchByNameAndDescription(@RequestParam String text) {
        log.info("Выполняется запрос поиска по имени и описанию. Текст запроса: " + text);
        return itemService.search(text);
    }
}
