package ru.yandex.practicum.filmorate.exception.genre;

public class IncorrectGenreIdException extends RuntimeException {
    public IncorrectGenreIdException(String message) {
        super(message);
    }
}