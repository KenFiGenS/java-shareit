package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

public interface BookingService {

    BookingDto createBooking(long userId, BookingDtoCreate bookingDto);

    BookingDto updateBooking(BookingDto bookingDto);

    BookingDto getBookingById(long id);

    BookingDto getAllBooking();
}
