package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserMapper;

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
        User currentUser = UserMapper.toUser(userService.getUserById(userId));
        Item currentItem = itemRepository.getReferenceById(bookingDto.getItemId());
        if (!currentItem.getAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }
        Booking currentBooking = BookingMapper.toBookingForCreating(bookingDto, currentUser, currentItem);
        return BookingMapper.toBookingDto(bookingRepository.save(currentBooking));
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto) {
        return null;
    }

    @Override
    public BookingDto getBookingById(long id) {
        return null;
    }

    @Override
    public BookingDto getAllBooking() {
        return null;
    }
}
