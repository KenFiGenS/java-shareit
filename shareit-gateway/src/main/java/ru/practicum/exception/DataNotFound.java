package ru.practicum.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataNotFound extends RuntimeException {

    public DataNotFound(String message) {
        super(message);
    }
}
