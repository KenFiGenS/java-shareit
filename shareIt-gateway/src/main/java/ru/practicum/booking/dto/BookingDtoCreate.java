package ru.practicum.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.exception.Marker;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoCreate {
    private long id;
    @NotNull(groups = Marker.OnCreate.class)
    @Future(groups = Marker.OnCreate.class)
    private LocalDateTime start;
    @NotNull(groups = Marker.OnCreate.class)
    private LocalDateTime end;
    @NotNull(groups = Marker.OnCreate.class)
    @Positive(groups = Marker.OnCreate.class)
    private long itemId;
}
