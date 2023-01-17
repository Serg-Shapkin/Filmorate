package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.film.IncorrectFilmIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO FILM(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        saveGenres(film);
        log.info("Добавлен фильм {}", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update("UPDATE FILM SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? WHERE FILM_ID = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        saveGenres(film);
        log.info("Фильм {} успешно обновлен", film.getName());
        return film;
    }

    @Override
    public List<Film> getAll() {
        SqlRowSet filmRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM FILM AS f INNER JOIN MPA AS m ON m.ID = f.RATING_ID");
        List<Film> films = new ArrayList<>();
        while (filmRowSet.next()) {
            films.add(makeFilm(filmRowSet));
        }
        if (films.isEmpty()) {
            log.error("В базе не сохранено ни одного фильма");
        }
        log.info("Запрошены все фильмы из базы данных");
        return films;
    }

    @Override
    public Film getById(Integer id) {
        SqlRowSet filmRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM FILM AS f INNER JOIN MPA AS m ON m.ID = f.RATING_ID WHERE FILM_ID = ?", id);
        filmRowSet.next();
        if (filmRowSet.last()) {
            log.info("Запрошен фильм с id={}", id);
            return makeFilm(filmRowSet);
        } else {
            log.error("Указан некорректный id фильма {}", id);
            throw new IncorrectFilmIdException(String.format("Указан некорректный id фильма: %s", id));
        }
    }

    @Override
    public List<Film> getPopular(Integer size) {
        String sql = "SELECT * FROM FILM AS f, MPA AS m WHERE f.RATING_ID = m.ID ORDER BY RATE DESC LIMIT ?";
        List<Film> popularFilms = jdbcTemplate.query(sql, (resultSet, rowNum) -> makePopularFilm(resultSet), size);
        log.info("Запрошен список из {} самых популярных фильмов", size);
        return popularFilms;
    }

    private Film makePopularFilm(ResultSet resultSet) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("FILM_ID"));
        film.setName(resultSet.getString("NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setReleaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(resultSet.getInt("DURATION"));
        film.setRate(resultSet.getInt("RATE"));
        Rating rating = new Rating();
        rating.setId(resultSet.getInt("ID"));
        rating.setName(resultSet.getString("RATING"));
        film.setMpa(rating);
        return film;
    }

    private Film makeFilm(SqlRowSet filmRowSet) {
        Film film = new Film();
        film.setId(filmRowSet.getInt("FILM_ID"));
        film.setName(filmRowSet.getString("NAME"));
        film.setDescription(filmRowSet.getString("DESCRIPTION"));
        film.setReleaseDate(Objects.requireNonNull(filmRowSet.getDate("RELEASE_DATE")).toLocalDate());
        film.setDuration(filmRowSet.getInt("DURATION"));
        film.setRate(filmRowSet.getInt("RATE"));
        Rating rating = new Rating();
        rating.setId(filmRowSet.getInt("ID"));
        rating.setName(filmRowSet.getString("RATING"));
        film.setMpa(rating);
        return film;
    }

    private void saveGenres(Film film) {
        if (film.getGenres() != null || !film.getGenres().isEmpty()) {
            jdbcTemplate.update("DELETE FROM GENRE_FILM WHERE FILM_ID = ?", film.getId());

            List<Genre> genres = new ArrayList<>(film.getGenres());
            jdbcTemplate.batchUpdate("INSERT INTO GENRE_FILM(FILM_ID, GENRE_ID) VALUES (?, ?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genres.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
        }
    }
}