package ru.practicum.shareit.exceptionControllers;


public class NotFoundBookingStatusException extends Exception{

    public NotFoundBookingStatusException(String s) {
        super(s);
    }
}
