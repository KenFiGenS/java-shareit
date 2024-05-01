package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService{
    private UserService userService;
    private ItemRequestRepository itemRequestRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = UserMapper.toUser(userService.getUserById(userId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        userService.getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(requestId);
        if (itemRequest == null) {
            throw new DataNotFound("ItemRequest под ID: " + requestId + " не найден");
        }
        List<ItemDto> itemsByRequest;
        List<Item> itemFromRepository = itemRepository.findItemByItemRequestId(requestId);
        if (itemFromRepository == null) {
            return ItemRequestMapper.toItemRequestDto(itemRequest, List.of());
        }
        itemsByRequest = itemFromRepository.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemsByRequest);
    }

    @Override
    public List<ItemRequestDto> getRequestsByUser(long userId) {
        List<Item> allItem = itemRepository.findAll();
        return itemRequestRepository.findByRequesterId(userId).stream()
                .map(itemRequest -> getItemRequestWithItem(itemRequest, allItem))
                .collect(Collectors.toList());
    }

    private ItemRequestDto getItemRequestWithItem(ItemRequest itemRequest, List<Item> allItem) {
        List<ItemDto> itemsByRequestId = allItem.stream()
                .filter(i -> i.getItemRequest() != null && i.getItemRequest().getId() == itemRequest.getId())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemsByRequestId);
    }
}
