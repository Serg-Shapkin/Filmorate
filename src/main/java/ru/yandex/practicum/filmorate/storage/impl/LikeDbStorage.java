package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Integer id, Integer userId) {
        jdbcTemplate.update("INSERT INTO LIKES(FILM_ID, USER_ID) VALUES (?, ?)", id, userId);
        updateRate(id);
    }

    @Override
    public void remove(Integer id, Integer userId) {
        jdbcTemplate.update("DELETE FROM LIKES WHERE USER_ID = ? AND FILM_ID = ?", userId, id);
        updateRate(id);
    }

    private void updateRate(Integer filmId) {
        String sqlQuery = "UPDATE FILM f SET RATE = (SELECT count(l.USER_ID) from LIKES l where l.FILM_ID = f.FILM_ID) WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }
}
