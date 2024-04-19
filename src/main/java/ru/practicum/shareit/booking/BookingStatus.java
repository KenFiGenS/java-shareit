package ru.practicum.shareit.booking;

import lombok.Data;

import javax.persistence.Enumerated;


public enum BookingStatus {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
