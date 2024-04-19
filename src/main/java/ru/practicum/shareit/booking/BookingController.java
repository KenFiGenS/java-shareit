package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptionControllers.Marker;

import javax.websocket.server.PathParam;
import java.util.List;

import static ru.practicum.shareit.constans.Constants.REQUEST_HEADER_NAME;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    private BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                    @Validated({Marker.OnCreate.class}) @RequestBody BookingDtoCreate bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmationBooking(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        return bookingService.confirmationBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                     @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBooker(@RequestHeader(REQUEST_HEADER_NAME) long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsByBooker(userId, state);
    }
}
