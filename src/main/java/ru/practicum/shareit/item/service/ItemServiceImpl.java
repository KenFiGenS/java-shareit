package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoItemById;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserMapper;

import javax.transaction.Transactional;
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
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @SneakyThrows
    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User currentUser = UserMapper.toUser(userService.getUserById(userId));
        Item item;
        if (itemDto.getRequestId() == 0) {
            item = ItemMapper.toItem(itemDto, currentUser, null);
        } else {
            ItemRequest itemRequest = itemRequestRepository.getReferenceById(itemDto.getRequestId());
            item = ItemMapper.toItem(itemDto, currentUser, itemRequest);
        }
        Item itemAfterCreate = itemRepository.save(item);
        return ItemMapper.toItemDto(itemAfterCreate);
    }

    @SneakyThrows
    @Override
    @Transactional
    public ItemDto updateItem(long userId, long id, ItemDto itemDto) {
        Item itemForUpdate = itemRepository.getReferenceById(id);
        if (itemForUpdate == null || itemForUpdate.getOwner().getId() != userId) {
            throw new DataNotFound("У данного пользователя нет вещи под ID: " + id);
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) itemForUpdate.setName(itemDto.getName());
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank())
            itemForUpdate.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) itemForUpdate.setAvailable(itemDto.getAvailable());
        if (itemDto.getRequestId() > 0) itemForUpdate.setItemRequest(itemRequestRepository.getReferenceById(itemDto.getRequestId()));
        Item itemAfterUpdate = itemRepository.save(itemForUpdate);
        return ItemMapper.toItemDto(itemAfterUpdate);
    }

    @Override
    @Transactional
    public ItemDtoWithBookingAndComments getItemById(long userId, long id) {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookingsById = bookingRepository.findByItemId(id);
        List<Booking> bookingsInPast = bookingsById.stream()
                .filter(booking -> booking.getStart().isBefore(currentTime))
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
        if (lastBooking != null && !lastBooking.getStatus().equals(BookingStatus.REJECTED)) {
            lastBookingDto = BookingMapper.bookingDtoItemByIdDtoItem(lastBooking);
        }
        if (nextBooking != null && !nextBooking.getStatus().equals(BookingStatus.REJECTED)) {
            nextBookingDto = BookingMapper.bookingDtoItemByIdDtoItem(nextBooking);
        }
        List<CommentDto> commentsDto = commentRepository.findAllByItemId(id).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());

        return ItemMapper.itemDtoWithBookingAndComments(currentItem,
                lastBookingDto,
                nextBookingDto,
                commentsDto);
    }

    @Override
    @Transactional
    public List<ItemDtoWithBookingAndComments> getAllItemByOwner(long userId) {
        List<Item> itemsByOwner = itemRepository.findItemByOwnerId(userId);
        return itemsByOwner.stream().map(item -> getItemById(userId, item.getId()))
                .sorted(Comparator.comparing(ItemDtoWithBookingAndComments::getId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public CommentDto createComment(long userId, long itemId, CommentDto commentDto) {
        LocalDateTime currentTime = LocalDateTime.now();
        User cuttentUser = UserMapper.toUser(userService.getUserById(userId));
        Item item = itemRepository.getReferenceById(itemId);
        List<Booking> bookingsByItemId = bookingRepository.findByBookerIdAndItemIdAndEndIsBefore(userId, itemId, currentTime);
        if (bookingsByItemId == null || bookingsByItemId.isEmpty()) {
            throw new IllegalArgumentException("Данный пользователь не брал вещь в аренду, либо аренда ещё не завершена");
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toCommentForCreated(commentDto, item, cuttentUser)));
    }
}
