package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoItemById;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptionControllers.DataNotFound;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

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
    public ItemDtoWithBooking getItemById(long userId, long id) {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookingsById = bookingRepository.findByItemId(id);
        List<Booking> bookingsInPast = bookingsById.stream()
                .filter(booking -> booking.getEnd().isBefore(currentTime))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .collect(Collectors.toList());
        List<Booking> bookingsInFuture = bookingsById.stream()
                .filter(booking -> booking.getStart().isAfter(currentTime))
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());
        Booking lastBooking = null;
        Booking nextBooking = null;
        Item currentItem = itemRepository.getReferenceById(id);
        if (userId == currentItem.getOwner().getId()) {
            if (!bookingsInPast.isEmpty()) {
                lastBooking = bookingsInPast.get(0);
            }
            if (!bookingsInFuture.isEmpty()) {
                nextBooking = bookingsInFuture.get(0);
            }
        }
        BookingDtoItemById lastBookingDto = null;
        BookingDtoItemById nextBookingDto = null;
        if (lastBooking != null) {
            lastBookingDto =  BookingMapper.bookingDtoItemByIdDtoItem(lastBooking);
        }
        if (nextBooking != null) {
            nextBookingDto = BookingMapper.bookingDtoItemByIdDtoItem(nextBooking);
        }
        return ItemMapper.itemDtoWithBooking(currentItem,
                lastBookingDto,
                nextBookingDto);
    }

    @Override
    public List<ItemDtoWithBooking> getAllItemByOwner(long userId) {
        List<Item> itemsByOwner = itemRepository.findItemByOwnerId(userId);
        return itemsByOwner.stream().map(item -> getItemById(userId, item.getId()))
                .sorted(Comparator.comparing(ItemDtoWithBooking::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.searchByNameAndDescription(text);
        return items.stream()
                .map(ItemMapper::toItemDto)
                .filter(ItemDto::getAvailable)
                .distinct()
                .collect(Collectors.toList());
    }
}
