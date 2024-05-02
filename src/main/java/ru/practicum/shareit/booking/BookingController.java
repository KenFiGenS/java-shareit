package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.Marker;

import java.util.List;

import static ru.practicum.shareit.constans.Constants.REQUEST_HEADER_NAME;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                    @Validated({Marker.OnCreate.class}) @RequestBody BookingDtoCreate bookingDto) {
        log.info("Выполняется запрос на бронирование вещи под ID: {} от пользователя под ID: {}", bookingDto.getId(), userId);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmationBooking(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        log.info("Выполняется запрос на изменение статуса бронирования под ID: {} от пользователя под ID: {}", bookingId, userId);
        return bookingService.confirmationBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                     @PathVariable long bookingId) {
        log.info("Выполняется запрос на получение информации о бронировании под ID: {} от пользователя под ID: {}", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBooker(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                                   @RequestParam (defaultValue = "0") int from,
                                                   @RequestParam (defaultValue = "5") int size,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        log.info("Выполняется запрос на получение всех бронирований от пользователя под ID: {}, со статусом: {}", userId, state);
        return bookingService.getAllBookingsByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingByOwner(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                                 @RequestParam (defaultValue = "0") int from,
                                                 @RequestParam (defaultValue = "5") int size,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        log.info("Выполняется запрос на получение всех бронирований от владельца под ID: {}, со статусом: {}", userId, state);
        return bookingService.getAllBookingsByOwner(userId, state, from, size);
    }
}
