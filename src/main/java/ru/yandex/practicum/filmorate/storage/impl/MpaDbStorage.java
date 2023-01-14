package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Rating> getAll() {
        SqlRowSet ratingRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM MPA");
        List<Rating> ratings = new ArrayList<>();
        while (ratingRowSet.next()) {
            ratings.add(makeRating(ratingRowSet));
        }
        return ratings;
    }

    @Override
    public Rating getById(Integer id) {
        SqlRowSet ratingRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM MPA WHERE ID = ?", id);
        ratingRowSet.next();
        if (ratingRowSet.last()) {
            return makeRating(ratingRowSet);
        } else {
            return null;
        }
    }

    private Rating makeRating(SqlRowSet ratingRowSet) {
        Rating rating = new Rating();
        rating.setId(ratingRowSet.getInt("ID"));
        rating.setName(ratingRowSet.getString("RATING"));
        return rating;
    }
}
