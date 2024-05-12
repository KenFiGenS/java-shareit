package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime current);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime current1, LocalDateTime current2);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime current);

    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemOwnerId(Long bookerId);

    List<Booking> findByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime current);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime current1, LocalDateTime current2);

    List<Booking> findByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime current);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findByBookerIdAndItemIdAndEndIsBefore(long userId, long itemId, LocalDateTime currentTime);

    Page<Booking> findByBookerIdOrderByStartDesc(long id, Pageable pageable);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(long id, Pageable pageable);
}
