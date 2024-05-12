package ru.practicum.shareit.exception;

public class RequestParameterIsEmpty extends Exception {

    public RequestParameterIsEmpty(String error) {
        super(error);
    }
}
