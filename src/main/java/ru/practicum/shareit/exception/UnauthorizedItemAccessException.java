package ru.practicum.shareit.exception;

public class UnauthorizedItemAccessException extends RuntimeException {

    public UnauthorizedItemAccessException(String message) {
        super(message);
    }
}