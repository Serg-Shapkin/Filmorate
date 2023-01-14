package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface MpaStorage {
    List<Rating> getAll();
    Rating getById(Integer id);
}
