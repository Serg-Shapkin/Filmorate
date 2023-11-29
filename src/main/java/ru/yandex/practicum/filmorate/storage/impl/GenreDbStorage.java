package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

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

    @Override
    public void filmAddGenres(List<Film> films) {
        final Map<Integer, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        // генерируем строку заполнитель, сод.символы film.size() ?
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        // помещаем строку в предложение IN оператора SQL
        String sql = String.format("SELECT gf.FILM_ID, g.ID, g.GENRE FROM GENRE_FILM AS gf JOIN GENRE AS g ON g.ID = gf.GENRE_ID WHERE gf.FILM_ID IN (%s)", inSql);
        jdbcTemplate.query(sql, filmById.keySet().toArray(new Integer[0]), (resultSet, rowNum) -> filmById.get(resultSet.getInt("FILM_ID")).getGenres().add(makeGenreRs(resultSet)));
    }

    private Genre makeGenreRs(ResultSet resultSet) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("ID"));
        genre.setName(resultSet.getString("GENRE"));
        return genre;
    }

    private Genre makeGenre(SqlRowSet genreRowSet) {
        Genre genre = new Genre();
        genre.setId(genreRowSet.getInt("ID"));
        genre.setName(genreRowSet.getString("GENRE"));
        return genre;
    }
}

