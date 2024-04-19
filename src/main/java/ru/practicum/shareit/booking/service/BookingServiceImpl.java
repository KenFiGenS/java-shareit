package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptionControllers.DataNotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserMapper;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService{
    private BookingRepository bookingRepository;
    private ItemRepository itemRepository;
    private UserService userService;

    @Override
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
        if (currentItem.getOwner().getId() == userId) {
            throw new DataNotFound("Нельзя забронировать свою же вещь");
        }
        if (!currentItem.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }
        Booking currentBooking = BookingMapper.toBookingForCreating(bookingDto, currentUser, currentItem);
        return BookingMapper.toBookingDto(bookingRepository.save(currentBooking));
    }

    @Override
    public BookingDto confirmationBooking(long userId, long bookingId, boolean approved) {
        Booking currentBooking = bookingRepository.getReferenceById(bookingId);
        Item currentItem = currentBooking.getItem();
        if (currentBooking.getBooker().getId() == userId) {
            throw new EntityNotFoundException("Изменять статус заявки может только владелец");
        }
        if (currentItem.getOwner().getId() != userId) {
            throw new IllegalArgumentException("Вещь не принадлежит пользователю");
        }
        if (approved ) {
            if (currentBooking.getStatus().equals(BookingStatus.APPROVED)) {
                throw  new IllegalArgumentException("Бронирование уже одобренно");
            } else {
                currentBooking.setStatus(BookingStatus.APPROVED);
                bookingRepository.save(currentBooking);
            }
        } else {
            currentBooking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(currentBooking);
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto) {

        return null;
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        Booking currentBooking = bookingRepository.getReferenceById(bookingId);
        Item currentItem = currentBooking.getItem();
        if (currentItem.getOwner().getId() != userId && currentBooking.getBooker().getId() != userId) {
            throw new DataNotFound("Текущее бронирование и вещь не принадлежит пользователю");
        }
        return BookingMapper.toBookingDto(currentBooking);
    }

    @Override
    public List<BookingDto> getAllBookingsByBooker(long userId, String state) {
        userService.getUserById(userId);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> currentBookingList;
        if (state.equals("ALL")) {
            currentBookingList = bookingRepository.findByBookerId(userId);
        } else if (state.equals("CURRENT")) {
            currentBookingList = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(
                    userId,
                    currentTime,
                    currentTime);
        } else if (state.equals("FUTURE")) {
            currentBookingList = bookingRepository.findByBookerIdAndStartIsAfter(userId, currentTime);
        } else if (state.equals("**PAST**")) {
            currentBookingList = bookingRepository.findByBookerIdAndEndIsBefore(userId, currentTime);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown state: " + state);
        }
        return currentBookingList.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(long userId, String state) {
        userService.getUserById(userId);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> currentBookingList;
        if (state.equals("ALL")) {
            currentBookingList = bookingRepository.findByItemOwnerId(userId);
        } else if (state.equals("CURRENT")) {
            currentBookingList = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                    userId,
                    currentTime,
                    currentTime);
        } else if (state.equals("FUTURE")) {
            currentBookingList = bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, currentTime);
        } else if (state.equals("**PAST**")) {
            currentBookingList = bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, currentTime);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown state: " + state);
        }
        return currentBookingList.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto getAllBooking() {

        return null;
    }


}
