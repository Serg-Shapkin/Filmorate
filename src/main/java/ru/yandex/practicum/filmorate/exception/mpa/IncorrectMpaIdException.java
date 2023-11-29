package ru.yandex.practicum.filmorate.exception.mpa;

public class IncorrectMpaIdException extends RuntimeException {
    public IncorrectMpaIdException(String message) {
        super(message);
    }
}
