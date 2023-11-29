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
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class LikeDbStorageTest {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    @Test
    @DisplayName("Проверка постановки лайка фильму")
    public void testAddLikeFilm() {
        Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1, 15), 90, 0, new Rating(1, null), new LinkedHashSet<>());
        Film film2 = new Film(0, "TestFilm2", "Description_2", LocalDate.of(2022, 2, 25), 90, 0, new Rating(2, null), new LinkedHashSet<>());
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));

        filmStorage.add(film1);
        filmStorage.add(film2);
        userStorage.add(user1);

        likeStorage.add(film2.getId(), user1.getId());

        List<Film> popular = filmStorage.getPopular(1);

        assertEquals(1, popular.get(0).getRate(), "Количество лайков не совпадает");
    }
    @Test
    @DisplayName("Проверка удаления лайка фильму")
    public void testRemoveLikeFilm() {
        Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1, 15), 90, 0, new Rating(1, null), new LinkedHashSet<>());
        Film film2 = new Film(0, "TestFilm2", "Description_2", LocalDate.of(2022, 2, 25), 90, 0, new Rating(2, null), new LinkedHashSet<>());
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));

        filmStorage.add(film1);
        filmStorage.add(film2);
        userStorage.add(user1);

        likeStorage.add(film2.getId(), user1.getId());
        likeStorage.remove(film2.getId(), user1.getId());

        List<Film> popular = filmStorage.getPopular(1);

        assertEquals(0, popular.get(0).getRate(), "Количество лайков не совпадает");
    }
}
