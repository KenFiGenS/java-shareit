package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptionControllers.DataNotFound;
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
        item.setOwner(cuttentUser);
        Item itemAfterCreate = itemRepository.save(item);
        return ItemMapper.toItemDto(itemAfterCreate);
    }

    @SneakyThrows
    @Override
    public ItemDto updateItem(long userId, long id, ItemDto itemDto) {
        Item itemForUpdate = itemRepository.getReferenceById(id);
        if (itemForUpdate.getOwner().getId() != userId) {
            throw new DataNotFound("У данного пользователя нет вещи под ID: " + id);
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) itemForUpdate.setName(itemDto.getName());
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) itemForUpdate.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) itemForUpdate.setAvailable(itemDto.getAvailable());
        Item itemAfterUpdate = itemRepository.save(itemForUpdate);
        return ItemMapper.toItemDto(itemAfterUpdate);
    }

    @Override
    public ItemDto getItemById(long userId, long id) {
        return ItemMapper.toItemDto(itemRepository.getReferenceById(id));
    }

    @Override
    public List<ItemDto> getAllItemByOwner(long userId) {
        List<Item> itemsByUser = itemRepository.findItemByOwnerId(userId);
        return itemsByUser.stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        List<Item> itemsByName = itemRepository.findItemByNameContainingIgnoreCase(text);
        List<Item> itemsByDescription = itemRepository.findItemByDescriptionContainingIgnoreCase(text);
        itemsByName.addAll(itemsByDescription);
        return itemsByName.stream()
                .map(ItemMapper::toItemDto)
                .filter(ItemDto::getAvailable)
                .distinct()
                .collect(Collectors.toList());
    }
}
