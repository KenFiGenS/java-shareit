package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFound;
import ru.practicum.shareit.exception.NotFoundBookingStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserMapper;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserService userService;

    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingDtoCreate bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Неверно заданы временные промежутки использования вещи");
        }
        List<Booking> allBookingByCurrentItemId = bookingRepository.findByItemId(bookingDto.getItemId());
        allBookingByCurrentItemId
                .forEach(b -> {
                    if (bookingDto.getStart().isAfter(b.getStart()) && bookingDto.getStart().isBefore(b.getEnd()))
                        throw new IllegalArgumentException("Имеется бронирование на данный промежуток времени");
                });
        User currentUser = UserMapper.toUser(userService.getUserById(userId));
        Item currentItem = itemRepository.getReferenceById(bookingDto.getItemId());
        if (currentItem.getOwner().getId() == currentUser.getId()) {
            throw new DataNotFound("Нельзя забронировать свою же вещь");
        }
        if (!currentItem.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }
        Booking currentBooking = BookingMapper.toBookingForCreating(bookingDto, currentUser, currentItem);
        return BookingMapper.toBookingDto(bookingRepository.save(currentBooking));
    }

    @Override
    @Transactional
    public BookingDto confirmationBooking(long userId, long bookingId, boolean approved) {
        Booking currentBooking = bookingRepository.getReferenceById(bookingId);
        Item currentItem = currentBooking.getItem();
        if (currentBooking.getBooker().getId() == userId) {
            throw new EntityNotFoundException("Изменять статус заявки может только владелец");
        }
        if (currentItem.getOwner().getId() != userId) {
            throw new IllegalArgumentException("Вещь не принадлежит пользователю");
        }
        if (approved) {
            if (currentBooking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new IllegalArgumentException("Бронирование уже одобренно");
            } else {
                currentBooking.setStatus(BookingStatus.APPROVED);
                bookingRepository.save(currentBooking);
            }
        } else {
            currentBooking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(currentBooking);
        }
        return BookingMapper.toBookingDto(currentBooking);
    }

    @Override
    @Transactional
    public BookingDto getBookingById(long userId, long bookingId) {
        Booking currentBooking = bookingRepository.getReferenceById(bookingId);
        Item currentItem = currentBooking.getItem();
        if (currentItem.getOwner().getId() != userId && currentBooking.getBooker().getId() != userId) {
            throw new DataNotFound("Текущее бронирование и вещь не принадлежит пользователю");
        }
        return BookingMapper.toBookingDto(currentBooking);
    }

    @SneakyThrows
    @Override
    @Transactional
    public List<BookingDto> getAllBookingsByBooker(long userId, String state, int from, int size) {
        userService.getUserById(userId);
        if (from == 0 && size == 0) {
            throw new IllegalArgumentException("Неверный индекс начального элемента и размера страницы");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Неверный индекс размера страницы");
        }
        if (from < 0) {
            throw new IllegalArgumentException("Неверный индекс начального элемента");
        }
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> currentBookingList;
        if (state.equals("ALL") && from > 0) {
            int currentPage = from / size;
            System.out.println(currentPage);
            Pageable pageable = PageRequest.of(currentPage, size);
            return bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("ALL")) {
            currentBookingList = bookingRepository.findByBookerId(userId);
        } else if (state.equals("CURRENT")) {
            currentBookingList = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                    userId,
                    currentTime,
                    currentTime);
        } else if (state.equals("FUTURE")) {
            currentBookingList = bookingRepository.findByBookerIdAndStartIsAfter(userId, currentTime);
        } else if (state.equals("PAST")) {
            currentBookingList = bookingRepository.findByBookerIdAndEndIsBefore(userId, currentTime);
        } else {
            if (BookingStatus.isInEnum(state, BookingStatus.class)) {
                currentBookingList = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.valueOf(state));
            } else {
                throw new NotFoundBookingStatusException("Unknown state: " + state);
            }

        }
        return currentBookingList.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    @Transactional
    public List<BookingDto> getAllBookingsByOwner(long userId, String state, int from, int size) {
        userService.getUserById(userId);
        if (from == 0 && size == 0) {
            throw new IllegalArgumentException("Неверный индекс начального элемента и размера страницы");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Неверный индекс размера страницы");
        }
        if (from < 0) {
            throw new IllegalArgumentException("Неверный индекс начального элемента");
        }
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> currentBookingList;
        if (state.equals("ALL") && from > 0) {
            int currentPage = from / size;
            System.out.println(currentPage);
            Pageable pageable = PageRequest.of(currentPage, size);
            return bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, pageable).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("ALL")) {
            currentBookingList = bookingRepository.findByItemOwnerId(userId);
        } else if (state.equals("CURRENT")) {
            currentBookingList = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                    userId,
                    currentTime,
                    currentTime);
        } else if (state.equals("FUTURE")) {
            currentBookingList = bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, currentTime);
        } else if (state.equals("PAST")) {
            currentBookingList = bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, currentTime);
        } else {
            if (BookingStatus.isInEnum(state, BookingStatus.class)) {
                currentBookingList = bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.valueOf(state));
            } else {
                throw new NotFoundBookingStatusException("Unknown state: " + state);
            }
        }
        return currentBookingList.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
