package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @SneakyThrows
    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User cuttentUser = UserMapper.toUser(userService.getUserById(userId));
        Item item = ItemMapper.toItem(itemDto, cuttentUser);
        Item itemAfterCreate = itemRepository.createItem(userId, item);
        return ItemMapper.toItemDto(itemAfterCreate);
    }

    @SneakyThrows
    @Override
    public ItemDto updateItem(long userId, long id, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto, UserMapper.toUser(userService.getUserById(userId)));
        Item itemAfterUpdate = itemRepository.updateItem(userId, id, item);
        return ItemMapper.toItemDto(itemAfterUpdate);
    }

    @Override
    public ItemDto getItemById(long userId, long id) {
        return ItemMapper.toItemDto(itemRepository.getItemById(userId, id));
    }

    @Override
    public List<ItemDto> getAllItemByOwner(long userId) {
        List<Item> itemsByUser = itemRepository.getAllItemByUser(userId);
        return itemsByUser.stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        text = text.toLowerCase();
        List<Item> itemsBySearch = itemRepository.search(text);
        return itemsBySearch.stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
