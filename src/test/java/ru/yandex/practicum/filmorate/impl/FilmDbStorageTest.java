package ru.yandex.practicum.filmorate.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class FilmDbStorageTest {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    @Test
    @DisplayName("Проверка добавления фильма")
    public void testAddFilm() {
        Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1, 15), 90, 0, new Rating(1, "PG"), new LinkedHashSet<>());
        filmStorage.add(film1);
        assertNotNull(film1, "Фильм не найден");
        assertEquals(1, filmStorage.getAll().size(), "Неверное количество фильмов");
    }


    @Test
    @DisplayName("Проверка обновления данных о фильме")
    void testUpdateFilm() {
        Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1, 15), 90, 0, new Rating(1, null), new LinkedHashSet<>());
        filmStorage.add(film1);

        final int filmId = film1.getId();
        Film savedFilm = filmStorage.getById(filmId);

        Film newFilm = new Film(1, "NewFilm", "Description_New", LocalDate.of(2022, 2, 25), 90, 0, new Rating(2, null), new LinkedHashSet<>());
        newFilm.setId(savedFilm.getId());

        savedFilm = newFilm;
        filmStorage.update(savedFilm);

        assertNotNull(savedFilm, "Фильм не найден");
        assertEquals(newFilm, savedFilm, "Фильмы не совпадают");

        final List<Film> films = new ArrayList<>(filmStorage.getAll());
        assertNotNull(films, "Фильмы не возвращаются");
        assertEquals(1, films.size(), "Неверное количество фильмов");
    }

    @Test
    @DisplayName("Проверка получения всех фильмов")
    void testGetAllFilms() {
        Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1, 15), 90, 0, new Rating(1, null), new LinkedHashSet<>());
        Film film2 = new Film(0, "TestFilm2", "Description_2", LocalDate.of(2022, 2, 25), 90, 0, new Rating(2, null), new LinkedHashSet<>());
        filmStorage.add(film1);
        filmStorage.add(film2);
        assertEquals(2, filmStorage.getAll().size(), "Неверное количество фильмов");
    }

    @Test
    @DisplayName("Проверка получения фильма по id")
    void TestGetFilmById() {
        Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1, 15), 90, 0, new Rating(2, "PG"), new LinkedHashSet<>());
        filmStorage.add(film1);

        final int filmId = film1.getId();
        final Film savedFilm = filmStorage.getById(filmId);

        assertNotNull(savedFilm, "Фильм не найден");
        assertEquals(filmId, film1.getId(), "Id фильмов не совпадают");
    }

    @Test
    @DisplayName("Проверка получения популярных фильмов")
    void TestGetPopular() {
        Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1, 15), 90, 0, new Rating(1, null), new LinkedHashSet<>());
        Film film2 = new Film(0, "TestFilm2", "Description_2", LocalDate.of(2022, 2, 25), 90, 0, new Rating(2, null), new LinkedHashSet<>());
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));

        filmStorage.add(film1);
        filmStorage.add(film2);
        userStorage.add(user1);

        likeStorage.add(film2.getId(), user1.getId());

        List<Film> popular = filmStorage.getPopular(1);

        assertEquals(film2.getId(), popular.get(0).getId(), "Id фильмов не совпадают");
        assertEquals(1, popular.get(0).getRate(), "Количество лайков не совпадает");
    }
}
