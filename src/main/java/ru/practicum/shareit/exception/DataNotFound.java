package ru.practicum.shareit.exception;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class DataNotFound extends RuntimeException {

    public DataNotFound(String message) {
        super(message);
    }
}
