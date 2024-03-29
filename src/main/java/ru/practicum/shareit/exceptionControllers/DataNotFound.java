package ru.practicum.shareit.exceptionControllers;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DataNotFound extends Exception{

    public DataNotFound(String message) {
        super(message);
    }
}
