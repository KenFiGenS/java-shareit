package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //FUTURE
    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime current);

    //CURRENT
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                              LocalDateTime current1,
                                                              LocalDateTime current2);
    //ALL
    List<Booking> findByBookerId(Long bookerId);

    List<Booking> findByItemId(Long itemId);

    //PAST
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId,
                                               LocalDateTime current);
}
