package ru.practicum.shareit.exceptions;

public class ItemIsUnavailableException extends RuntimeException {
    public ItemIsUnavailableException(String message) {
        super(message);
    }
}
