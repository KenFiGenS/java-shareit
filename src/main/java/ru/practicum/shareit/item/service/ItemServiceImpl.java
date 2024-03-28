package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.sql.SQLDataException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @SneakyThrows
    @Override
    public ItemDto createItem(long userId, Item item) {
        User cuttentUser = userRepository.getUserById(userId);
        if (cuttentUser == null) {
            throw new SQLDataException("Пользователь c ID: " + userId + " не найден!");
        }
        item.setOwner(cuttentUser);
        Item itemAfterCreate = itemRepository.createItem(userId, item);
        return ItemMapper.toItemDto(itemAfterCreate);
    }

    @Override
    public ItemDto updateItem(long userId, long id, Item item) {
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
        if (text.isEmpty()) {
            return List.of();
        }
        text = text.toLowerCase();
        List<Item> itemsBySearch = itemRepository.search(text);
        return itemsBySearch.stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
