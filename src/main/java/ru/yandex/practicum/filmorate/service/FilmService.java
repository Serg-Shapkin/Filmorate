package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film add(Film film);
    Film update(Film film);
    List<Film> getAll();
    Film getById(Integer id);

    Film addLike(Integer id, Integer userId);
    Film removeLike(Integer id, Integer userId);
    List<Film> getPopular(Integer size);
}