package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //FUTURE booker
    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime current);

    //CURRENT booker
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime current1, LocalDateTime current2);

    //PAST booker
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime current);

    //ALL booker
    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemId(Long itemId);

    //ALL owner
    List<Booking> findByItemOwnerId(Long bookerId);

    //FUTURE owner
    List<Booking> findByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime current);

    //CURRENT owner
    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime current1, LocalDateTime current2);

    //PAST owner
    List<Booking> findByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime current);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndStatus(Long userId, BookingStatus status);
}
