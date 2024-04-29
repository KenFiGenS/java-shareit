package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    BookingService bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserService userService;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                itemRepository,
                userService
        );
    }

    @Test
    void createBookingTestThrowIllegalArgumentExceptionIncorrectDate() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
                0,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(),
                1);
        assertThrows(IllegalArgumentException.class,
                ()-> bookingService.createBooking(1, bookingDtoCreate));
    }

    @Test
    void createBookingTestThrowIllegalArgumentExceptionItemHaveBookingInThisDate() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
                0,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(4),
                1);

        when(bookingRepository.findByItemId(anyLong())).thenReturn(bookingsFromRepository);

        assertThrows(IllegalArgumentException.class,
                ()-> bookingService.createBooking(1, bookingDtoCreate));
    }

    @Test
    void createBookingTestThrowDataNotFoundExceptionNotAvailable() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
                0,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                1);

        when(bookingRepository.findByItemId(anyLong())).thenReturn(bookingsFromRepository);
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(owner));
        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemForBooking);

        assertThrows(DataNotFound.class,
                ()-> bookingService.createBooking(1, bookingDtoCreate));
    }

    @Test
    void createBookingTestThrowIllegalArgumentExceptionNotAvailable() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", false, owner);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
                0,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                1);

        when(bookingRepository.findByItemId(anyLong())).thenReturn(bookingsFromRepository);
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemForBooking);

        assertThrows(IllegalArgumentException.class,
                ()-> bookingService.createBooking(1, bookingDtoCreate));
    }



    @Test
    void createBookingTest() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
                0,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                1);
        Booking bookingAfterCreate = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.WAITING
        );

        when(bookingRepository.findByItemId(anyLong())).thenReturn(bookingsFromRepository);
        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(itemRepository.getReferenceById(anyLong())).thenReturn(itemForBooking);
        when(bookingRepository.save(any())).thenReturn(bookingAfterCreate);

        BookingDto bookingDtoResult =  bookingService.createBooking(1, bookingDtoCreate);
        assertEquals(3, bookingDtoResult.getId());
        assertEquals(1, bookingDtoResult.getBooker().getId());
        assertEquals(1, bookingDtoResult.getItem().getId());
        assertEquals(BookingStatus.WAITING, bookingDtoResult.getStatus());
        verify(bookingRepository).save(any());
    }

    @Test
    void confirmationBookingTestThrowEntityNotFoundException() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingAfterCreate = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.WAITING
        );

        when(bookingRepository.getReferenceById(anyLong())).thenReturn(bookingAfterCreate);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.confirmationBooking(1, 3, true));
    }

    @Test
    void confirmationBookingTestThrowIllegalArgumentExceptionNotOwner() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingAfterCreate = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.WAITING
        );

        when(bookingRepository.getReferenceById(anyLong())).thenReturn(bookingAfterCreate);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.confirmationBooking(3, 3, true));
    }

    @Test
    void confirmationBookingTestThrowIllegalArgumentExceptionIsApproved() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingAfterCreate = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );

        when(bookingRepository.getReferenceById(anyLong())).thenReturn(bookingAfterCreate);

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.confirmationBooking(2, 3, true));
    }

    @Test
    void confirmationBookingTestApproved() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingAfterCreate = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.WAITING
        );
        Booking bookingAfterApproved = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );

        when(bookingRepository.getReferenceById(anyLong())).thenReturn(bookingAfterCreate);
        when(bookingRepository.save(any())).thenReturn(bookingAfterApproved);

        BookingDto bookingDtoResult = bookingService.confirmationBooking(2, 3, true);
        assertEquals(3, bookingDtoResult.getId());
        assertEquals(BookingStatus.APPROVED, bookingDtoResult.getStatus());
        verify(bookingRepository).save(any());
    }

    @Test
    void confirmationBookingTestRejected() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingAfterCreate = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.WAITING
        );
        Booking bookingAfterApproved = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.REJECTED
        );

        when(bookingRepository.getReferenceById(anyLong())).thenReturn(bookingAfterCreate);
        when(bookingRepository.save(any())).thenReturn(bookingAfterApproved);

        BookingDto bookingDtoResult = bookingService.confirmationBooking(2, 3, false);
        assertEquals(3, bookingDtoResult.getId());
        assertEquals(BookingStatus.REJECTED, bookingDtoResult.getStatus());
        verify(bookingRepository).save(any());
    }


    @Test
    void getBookingByIdTestThrowDataNotFound() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromRepository = new Booking(1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                itemForBooking,
                booker,
                BookingStatus.APPROVED);

        when(bookingRepository.getReferenceById(anyLong())).thenReturn(bookingFromRepository);

        assertThrows(DataNotFound.class,
                () -> bookingService.getBookingById(3, 1));
    }

    @Test
    void getBookingByIdTest() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromRepository = new Booking(1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                itemForBooking,
                booker,
                BookingStatus.APPROVED);

        when(bookingRepository.getReferenceById(anyLong())).thenReturn(bookingFromRepository);

        BookingDto bookingDtoResult = bookingService.getBookingById(1, 1);
        assertEquals(1, bookingDtoResult.getId());
    }

    @Test
    void getAllBookingsByBookerTestStateAll() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromFuture = new Booking(
                1,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        Booking bookingFromPast = new Booking(
                2,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1),
                itemForBooking, booker,
                BookingStatus.APPROVED
        );
        Booking bookingFromCurrent = new Booking(
                3,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromFuture, bookingFromPast, bookingFromCurrent);

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(bookingRepository.findByBookerId(anyLong())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByBooker(1, "ALL");
        assertEquals(3, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByBookerTestStatePast() {
        LocalDateTime currentTime = LocalDateTime.now();
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromPast = new Booking(
                2,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1),
                itemForBooking, booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromPast);

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(bookingRepository.findByBookerIdAndEndIsBefore(anyLong(), any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByBooker(1, "PAST");
        assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByBookerTestStateCurrent() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromCurrent = new Booking(
                3,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromCurrent);

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByBooker(1, "CURRENT");
        assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByBookerTestStateFuture() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromFuture = new Booking(
                1,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromFuture);

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(bookingRepository.findByBookerIdAndStartIsAfter(anyLong(), any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByBooker(1, "FUTURE");
        assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByBookerTestStateApproved() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromFuture = new Booking(
                1,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        Booking bookingFromPast = new Booking(
                2,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1),
                itemForBooking, booker,
                BookingStatus.APPROVED
        );
        Booking bookingFromCurrent = new Booking(
                3,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromFuture, bookingFromPast, bookingFromCurrent);

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(bookingRepository.findByBookerIdAndStatus(anyLong(), any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByBooker(1, "APPROVED");
        assertEquals(3, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByBookerTestThrowNotFoundBookingStatusException() {
        User booker = new User(1, "email@mail.ru", "NameUser");

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));

        assertThrows(NotFoundBookingStatusException.class,
                () -> bookingService.getAllBookingsByBooker(1, "ERROR"));
    }

    @Test
    void getAllBookingsByOwnerTestTateAll() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromFuture = new Booking(
                1,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        Booking bookingFromPast = new Booking(
                2,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1),
                itemForBooking, booker,
                BookingStatus.APPROVED
        );
        Booking bookingFromCurrent = new Booking(
                3,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromFuture, bookingFromPast, bookingFromCurrent);

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(bookingRepository.findByItemOwnerId(anyLong())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByOwner(1, "ALL");
        assertEquals(3, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByOwnerTestTateCurrent() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromCurrent = new Booking(
                3,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromCurrent);

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(), any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByOwner(1, "CURRENT");
        assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByOwnerTestTateFuture() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromFuture = new Booking(
                1,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromFuture);

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(bookingRepository.findByItemOwnerIdAndStartIsAfter(anyLong(), any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByOwner(1, "FUTURE");
        assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByOwnerTestTatePast() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromPast = new Booking(
                2,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1),
                itemForBooking, booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromPast);

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(bookingRepository.findByItemOwnerIdAndEndIsBefore(anyLong(), any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByOwner(1, "PAST");
        assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByOwnerTestTateApproved() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner);
        Booking bookingFromFuture = new Booking(
                1,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        Booking bookingFromPast = new Booking(
                2,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1),
                itemForBooking, booker,
                BookingStatus.APPROVED
        );
        Booking bookingFromCurrent = new Booking(
                3,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromFuture, bookingFromPast, bookingFromCurrent);

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByOwner(1, "APPROVED");
        assertEquals(3, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByOwnerTestThrowNotFoundBookingStatusException() {
        User booker = new User(1, "email@mail.ru", "NameUser");

        when(userService.getUserById(anyLong())).thenReturn(UserMapper.toUserDto(booker));

        assertThrows(NotFoundBookingStatusException.class,
                () -> bookingService.getAllBookingsByOwner(1, "ERROR"));
    }

    @Test
    void getAllBooking() {
    }
}