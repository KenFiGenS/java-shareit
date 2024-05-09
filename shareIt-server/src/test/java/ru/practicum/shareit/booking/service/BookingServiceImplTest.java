package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(1, bookingDtoCreate));
    }

    @Test
    void createBookingTestThrowIllegalArgumentExceptionItemHaveBookingInThisDate() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
                0,
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(4),
                1);

        Mockito.when(bookingRepository.findByItemId(ArgumentMatchers.anyLong())).thenReturn(bookingsFromRepository);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(1, bookingDtoCreate));
    }

    @Test
    void createBookingTestThrowDataNotFoundExceptionNotAvailable() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
                0,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                1);

        Mockito.when(bookingRepository.findByItemId(ArgumentMatchers.anyLong())).thenReturn(bookingsFromRepository);
        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(owner));
        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemForBooking);

        Assertions.assertThrows(DataNotFound.class,
                () -> bookingService.createBooking(1, bookingDtoCreate));
    }

    @Test
    void createBookingTestThrowIllegalArgumentExceptionNotAvailable() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", false, owner, null);
        List<Booking> bookingsFromRepository = List.of(
                new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), itemForBooking, booker, BookingStatus.APPROVED),
                new Booking(2, LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusWeeks(1), itemForBooking, booker, BookingStatus.APPROVED)
        );
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(
                0,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                1);

        Mockito.when(bookingRepository.findByItemId(ArgumentMatchers.anyLong())).thenReturn(bookingsFromRepository);
        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemForBooking);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(1, bookingDtoCreate));
    }


    @Test
    void createBookingTest() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
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

        Mockito.when(bookingRepository.findByItemId(ArgumentMatchers.anyLong())).thenReturn(bookingsFromRepository);
        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(itemRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(itemForBooking);
        Mockito.when(bookingRepository.save(ArgumentMatchers.any())).thenReturn(bookingAfterCreate);

        BookingDto bookingDtoResult = bookingService.createBooking(1, bookingDtoCreate);
        Assertions.assertEquals(3, bookingDtoResult.getId());
        Assertions.assertEquals(1, bookingDtoResult.getBooker().getId());
        Assertions.assertEquals(1, bookingDtoResult.getItem().getId());
        Assertions.assertEquals(BookingStatus.WAITING, bookingDtoResult.getStatus());
        Mockito.verify(bookingRepository).save(ArgumentMatchers.any());
    }

    @Test
    void confirmationBookingTestThrowEntityNotFoundException() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingAfterCreate = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.WAITING
        );

        Mockito.when(bookingRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(bookingAfterCreate);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> bookingService.confirmationBooking(1, 3, true));
    }

    @Test
    void confirmationBookingTestThrowIllegalArgumentExceptionNotOwner() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingAfterCreate = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.WAITING
        );

        Mockito.when(bookingRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(bookingAfterCreate);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.confirmationBooking(3, 3, true));
    }

    @Test
    void confirmationBookingTestThrowIllegalArgumentExceptionIsApproved() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingAfterCreate = new Booking(
                3,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );

        Mockito.when(bookingRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(bookingAfterCreate);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.confirmationBooking(2, 3, true));
    }

    @Test
    void confirmationBookingTestApproved() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
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

        Mockito.when(bookingRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(bookingAfterCreate);
        Mockito.when(bookingRepository.save(ArgumentMatchers.any())).thenReturn(bookingAfterApproved);

        BookingDto bookingDtoResult = bookingService.confirmationBooking(2, 3, true);
        Assertions.assertEquals(3, bookingDtoResult.getId());
        Assertions.assertEquals(BookingStatus.APPROVED, bookingDtoResult.getStatus());
        Mockito.verify(bookingRepository).save(ArgumentMatchers.any());
    }

    @Test
    void confirmationBookingTestRejected() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
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

        Mockito.when(bookingRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(bookingAfterCreate);
        Mockito.when(bookingRepository.save(ArgumentMatchers.any())).thenReturn(bookingAfterApproved);

        BookingDto bookingDtoResult = bookingService.confirmationBooking(2, 3, false);
        Assertions.assertEquals(3, bookingDtoResult.getId());
        Assertions.assertEquals(BookingStatus.REJECTED, bookingDtoResult.getStatus());
        Mockito.verify(bookingRepository).save(ArgumentMatchers.any());
    }


    @Test
    void getBookingByIdTestThrowDataNotFound() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingFromRepository = new Booking(1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                itemForBooking,
                booker,
                BookingStatus.APPROVED);

        Mockito.when(bookingRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(bookingFromRepository);

        Assertions.assertThrows(DataNotFound.class,
                () -> bookingService.getBookingById(3, 1));
    }

    @Test
    void getBookingByIdTest() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingFromRepository = new Booking(1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                itemForBooking,
                booker,
                BookingStatus.APPROVED);

        Mockito.when(bookingRepository.getReferenceById(ArgumentMatchers.anyLong())).thenReturn(bookingFromRepository);

        BookingDto bookingDtoResult = bookingService.getBookingById(1, 1);
        Assertions.assertEquals(1, bookingDtoResult.getId());
    }

    @Test
    void getAllBookingsByBookerTestStateAll() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
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

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(bookingRepository.findByBookerId(ArgumentMatchers.anyLong())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByBooker(1, "ALL", 0, 20);
        Assertions.assertEquals(3, bookingDtoResult.size());

        Pageable pageRequest = PageRequest.of(1, 1);
        List<Booking> allBookings = List.of(bookingFromFuture, bookingFromPast);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), allBookings.size());
        List<Booking> pageContent = allBookings.subList(start, end);

        Mockito.when(bookingRepository.findByBookerIdOrderByStartDesc(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(new PageImpl<>(pageContent, pageRequest, allBookings.size()));

        bookingDtoResult = bookingService.getAllBookingsByBooker(1, "ALL", 1, 1);
        Assertions.assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByBookerTestStatePast() {
        LocalDateTime currentTime = LocalDateTime.now();
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingFromPast = new Booking(
                2,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1),
                itemForBooking, booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromPast);

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(bookingRepository.findByBookerIdAndEndIsBefore(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByBooker(1, "PAST", 0, 20);
        Assertions.assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByBookerTestStateCurrent() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingFromCurrent = new Booking(
                3,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromCurrent);

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByBooker(1, "CURRENT", 0, 20);
        Assertions.assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByBookerTestStateFuture() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingFromFuture = new Booking(
                1,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromFuture);

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(bookingRepository.findByBookerIdAndStartIsAfter(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByBooker(1, "FUTURE", 0, 20);
        Assertions.assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByBookerTestStateApproved() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
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

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(bookingRepository.findByBookerIdAndStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByBooker(1, "APPROVED", 0, 20);
        Assertions.assertEquals(3, bookingDtoResult.size());

    }

    @Test
    void getAllBookingsByBookerTestThrowIllegalArgumentExceptionSizeAndFromIsZero() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByBooker(1, "ALL", 0, 0));
    }

    @Test
    void getAllBookingsByBookerTestThrowIllegalArgumentExceptionSizeIsNegativeAndFromIsZero() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByBooker(1, "ALL", 0, -1));
    }

    @Test
    void getAllBookingsByBookerTestThrowIllegalArgumentExceptionFromIsNegative() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByBooker(1, "ALL", -1, 5));
    }

    @Test
    void getAllBookingsByBookerTestThrowNotFoundBookingStatusException() {
        User booker = new User(1, "email@mail.ru", "NameUser");

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));

        Assertions.assertThrows(NotFoundBookingStatusException.class,
                () -> bookingService.getAllBookingsByBooker(1, "ERROR", 0, 20));
    }

    @Test
    void getAllBookingsByOwnerTestTateAll() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
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

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(bookingRepository.findByItemOwnerId(ArgumentMatchers.anyLong())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByOwner(1, "ALL", 0, 5);
        Assertions.assertEquals(3, bookingDtoResult.size());

        Pageable pageRequest = PageRequest.of(1, 1);
        List<Booking> allBookings = List.of(bookingFromFuture, bookingFromPast);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), allBookings.size());
        List<Booking> pageContent = allBookings.subList(start, end);

        Mockito.when(bookingRepository.findByItemOwnerIdOrderByStartDesc(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(new PageImpl<>(pageContent, pageRequest, allBookings.size()));

        bookingDtoResult = bookingService.getAllBookingsByOwner(1, "ALL", 1, 1);
        Assertions.assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByOwnerTestTateCurrent() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingFromCurrent = new Booking(
                3,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(1),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromCurrent);

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByOwner(1, "CURRENT", 0, 5);
        Assertions.assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByOwnerTestTateFuture() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingFromFuture = new Booking(
                1,
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(5),
                itemForBooking,
                booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromFuture);

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartIsAfter(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByOwner(1, "FUTURE", 0, 5);
        Assertions.assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByOwnerTestTatePast() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
        Booking bookingFromPast = new Booking(
                2,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1),
                itemForBooking, booker,
                BookingStatus.APPROVED
        );
        List<Booking> bookings = List.of(bookingFromPast);

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(bookingRepository.findByItemOwnerIdAndEndIsBefore(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByOwner(1, "PAST", 0, 5);
        Assertions.assertEquals(1, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByOwnerTestTateApproved() {
        User booker = new User(1, "email@mail.ru", "NameUser");
        User owner = new User(2, "email2@mail.ru", "NameUser2");
        Item itemForBooking = new Item(1, "Hammer", "Big hammer", true, owner, null);
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

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));
        Mockito.when(bookingRepository.findByItemOwnerIdAndStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any())).thenReturn(bookings);

        List<BookingDto> bookingDtoResult = bookingService.getAllBookingsByOwner(1, "APPROVED", 0, 5);
        Assertions.assertEquals(3, bookingDtoResult.size());
    }

    @Test
    void getAllBookingsByOwnerTestThrowIllegalArgumentExceptionSizeAndFromIsZero() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByOwner(1, "ALL", 0, 0));
    }

    @Test
    void getAllBookingsByOwnerTestThrowIllegalArgumentExceptionSizeIsNegativeAndFromIsZero() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByOwner(1, "ALL", 0, -1));
    }

    @Test
    void getAllBookingsByOwnerTestThrowIllegalArgumentExceptionFromIsNegative() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingsByOwner(1, "ALL", -1, 5));
    }

    @Test
    void getAllBookingsByOwnerTestThrowNotFoundBookingStatusException() {
        User booker = new User(1, "email@mail.ru", "NameUser");

        Mockito.when(userService.getUserById(ArgumentMatchers.anyLong())).thenReturn(UserMapper.toUserDto(booker));

        Assertions.assertThrows(NotFoundBookingStatusException.class,
                () -> bookingService.getAllBookingsByOwner(1, "ERROR", 0, 5));
    }

    @Test
    void getAllBooking() {
    }
}