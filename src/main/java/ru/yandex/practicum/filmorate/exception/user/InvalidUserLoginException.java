package ru.yandex.practicum.filmorate.exception.user;

public class InvalidUserLoginException extends RuntimeException {
    public InvalidUserLoginException(String message) {
        super(message);
    }
}
