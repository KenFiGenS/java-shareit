package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.userDto.UserDto;
import ru.practicum.shareit.user.userDto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DirtiesContext
    void getAllBookingsByOwner() {
        UserDto booker = userService.create(new UserDto(0, "email@mail.ru", "NameUser"));
        UserDto owner1 = userService.create(new UserDto(0, "email2@mail.ru", "NameUser2"));
        UserDto owner2 = userService.create(new UserDto(0, "email3@mail.ru", "NameUser3"));
        Item itemForBooking1 = new Item(0, "Hammer", "Big hammer", true, UserMapper.toUser(owner1), null);
        Item itemForBooking2 = new Item(0, "Glasses", "Big Glasses", true, UserMapper.toUser(owner1), null);
        Item itemForBooking3 = new Item(0, "Camera", "Professional camera", true, UserMapper.toUser(owner2), null);
        long itemId1 = itemRepository.save(itemForBooking1).getId();
        itemRepository.save(itemForBooking2);
        itemRepository.save(itemForBooking3);

        bookingService.createBooking(booker.getId(), new BookingDtoCreate(
                0L,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemId1
        ));
        bookingService.createBooking(booker.getId(), new BookingDtoCreate(
                0L,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1),
                itemId1
        ));
        bookingService.createBooking(booker.getId(), new BookingDtoCreate(
                0L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2),
                itemId1
        ));

        bookingService.confirmationBooking(owner1.getId(), 1, true);
        bookingService.confirmationBooking(owner1.getId(), 2, false);
        bookingService.confirmationBooking(owner1.getId(), 3, false);

        List<BookingDto> bookingDtoAll = bookingService.getAllBookingsByOwner(2, "ALL", 0, 0);
        assertEquals(3, bookingDtoAll.size());
        List<BookingDto> bookingDtoApproved = bookingService.getAllBookingsByOwner(2, "APPROVED", 0, 0);
        assertEquals(1, bookingDtoApproved.size());
        List<BookingDto> bookingDtoRejected = bookingService.getAllBookingsByOwner(2, "REJECTED", 0, 0);
        assertEquals(2, bookingDtoRejected.size());
        List<BookingDto> bookingDtoCurrent = bookingService.getAllBookingsByOwner(2, "CURRENT", 0, 0);
        assertEquals(1, bookingDtoCurrent.size());
    }
}