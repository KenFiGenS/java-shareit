package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(long userId, BookingDtoCreate bookingDto);

    BookingDto getBookingById(long userId, long bookingId);

    BookingDto getAllBooking();

    BookingDto confirmationBooking(long userId, long bookingId, boolean approved);

    List<BookingDto> getAllBookingsByBooker(long userId, String state);

    List<BookingDto> getAllBookingsByOwner(long userId, String state);
}
