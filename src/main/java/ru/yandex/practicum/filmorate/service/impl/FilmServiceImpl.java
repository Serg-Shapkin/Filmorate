package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.film.FilmValidationException;
import ru.yandex.practicum.filmorate.exception.film.InvalidReleaseDateFilmException;
import ru.yandex.practicum.filmorate.exception.user.UserValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private static final LocalDate RELEASE_DATE = LocalDate.of(1895,12,28);
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final LikeStorage likeStorage;

    @Override
    public Film add(Film film) {
        filmReleaseDateValidation(film); // проверка даты релиза
        return filmStorage.add(film);
    }

    @Override
    public Film update(Film film) {
        filmReleaseDateValidation(film); // проверка даты релиза
        filmValidation(film.getId());
        return filmStorage.update(film);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(filmStorage.getAll());
    }

    @Override
    public Film getById(Integer id) {
        filmValidation(id);
        return filmStorage.getById(id);
    }

    @Override
    public Film addLike(Integer id, Integer userId) {
        filmValidation(id);
        userValidation(userId);

        likeStorage.add(id, userId);
        return filmStorage.getById(id);
    }

    @Override
    public Film removeLike(Integer id, Integer userId) {
        filmValidation(id);
        userValidation(userId);

        likeStorage.remove(id, userId);
        return filmStorage.getById(id);
    }

    @Override
    public List<Film> getPopular(Integer size) {
        return filmStorage.getPopular(size);
    }

    private void filmValidation(Integer id) {
        if (filmStorage.getById(id) == null) {
            throw new FilmValidationException("Фильм с id=" + id + " не найден в базе");
        }
    }

    private void filmReleaseDateValidation(Film film) {
        if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            throw new InvalidReleaseDateFilmException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
    }

    private void userValidation(Integer id) {
        if (userService.getById(id) == null) {
            throw new UserValidationException(String.format("Пользователь с id=%s не найден в базе", id));
        }
    }
}