package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        SqlRowSet genreRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE");
        List<Genre> genres = new ArrayList<>();
        while (genreRowSet.next()) {
            genres.add(makeGenre(genreRowSet));
        }
        return genres;
    }

    @Override
    public Genre getById(Integer id) {
        SqlRowSet genreRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE WHERE ID = ?", id);
        genreRowSet.next();
        if (genreRowSet.last()) {
            return makeGenre(genreRowSet);
        } else {
            return null;
        }
    }

    private Genre makeGenre(SqlRowSet genreRowSet) {
        Genre genre = new Genre();
        genre.setId(genreRowSet.getInt("ID"));
        genre.setName(genreRowSet.getString("GENRE"));
        return genre;
    }
}
