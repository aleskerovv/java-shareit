package ru.practicum.shareit.exceptions;

public class PaginationException extends RuntimeException {
    public PaginationException(String message) {
        super(message);
    }
}
