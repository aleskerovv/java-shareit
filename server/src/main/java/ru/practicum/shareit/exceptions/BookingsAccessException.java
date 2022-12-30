package ru.practicum.shareit.exceptions;

public class BookingsAccessException extends RuntimeException {
    public BookingsAccessException(String message) {
        super(message);
    }
}
