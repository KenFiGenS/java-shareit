package ru.practicum.shareit.booking.model;

import java.util.Arrays;

public enum BookingStatus {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    public static boolean isInEnum(String value, Class<BookingStatus> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants()).anyMatch(e -> e.name().equals(value));
    }
}
