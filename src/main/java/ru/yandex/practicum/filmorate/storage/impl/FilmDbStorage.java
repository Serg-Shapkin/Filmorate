package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.film.IncorrectFilmIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private int filmId = 0;
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        getMaxIdFilm();
    }

    @Override
    public Film add(Film film) {
        filmId++;
        film.setId(filmId);
        film.setRate(0);
        jdbcTemplate.update("INSERT INTO FILM VALUES (?, ?, ?, ?, ?, ?, ?)",
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId());
        if (film.getGenres() != null) {
            butchUpdate(film);
        }
        SqlRowSet filmRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM FILM AS f INNER JOIN MPA AS m ON m.ID = f.RATING_ID WHERE FILM_ID = ?", film.getId());
        filmRowSet.next();
        return makeFilm(filmRowSet);
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update("UPDATE FILM " +
                        "SET NAME = ?, " +
                        "DESCRIPTION = ?, " +
                        "RELEASE_DATE = ?, " +
                        "DURATION = ?, " +
                        "RATING_ID = ? WHERE FILM_ID = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            jdbcTemplate.update("DELETE FROM GENRE_FILM WHERE FILM_ID = ?", film.getId());
            butchUpdate(film);
        }
        SqlRowSet filmRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM FILM AS f INNER JOIN MPA AS m ON m.ID = f.RATING_ID WHERE FILM_ID = ?", film.getId());
        if (filmRowSet.next()) {
            log.info("Фильм {} успешно обновлен", film.getName());
            return makeFilm(filmRowSet);
        } else {
            return null;
        }
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
            return makeFilm(filmRowSet);
        } else {
            log.error("Указан некорректный id фильма {}", id);
            throw new IncorrectFilmIdException(String.format("Указан некорректный id фильма: %s", id));
        }
    }

    /**
     При текущей реализации в цикле вызывается метод getById, что не совсем корректно,
     так как на каждый его вызов происходит обращение к БД
     */
    @Override //  рабочий код
    public List<Film> getPopular(Integer size) {
        SqlRowSet popularFilmRowSet = jdbcTemplate.queryForRowSet("SELECT FILM_ID, RATE FROM FILM GROUP BY FILM_ID, RATE ORDER BY RATE DESC LIMIT ?", size);
        List<Film> films = new ArrayList<>();
        while (popularFilmRowSet.next()) {
            films.add(getById(popularFilmRowSet.getInt("FILM_ID")));
        }
        return films;
    }

    private Film makeFilm(SqlRowSet filmRowSet) {
        Film film = new Film();
        int filmId = filmRowSet.getInt("FILM_ID");

        film.setId(filmId);
        film.setName(filmRowSet.getString("NAME"));
        film.setDescription(filmRowSet.getString("DESCRIPTION"));
        film.setReleaseDate(Objects.requireNonNull(filmRowSet.getDate("RELEASE_DATE")).toLocalDate());
        film.setDuration(filmRowSet.getInt("DURATION"));
        film.setRate(filmRowSet.getInt("RATE"));

        // рейтинг
        film.setMpa(new Rating(filmRowSet.getInt("RATING_ID"), filmRowSet.getString("RATING")));

        // лайки
        Set<Integer> like = new HashSet<>();
        SqlRowSet likeRowSet = jdbcTemplate.queryForRowSet("SELECT USER_ID FROM LIKES WHERE FILM_ID = ?", filmId);
        while (likeRowSet.next()) {
            like.add(likeRowSet.getInt("USER_ID"));
        }

        // жанры
        /**
         Следует использовать отдельный метод в DAO-жанров, который будет принимать коллекцию фильмов и добавлять в каждую из коллекций фильмов полученные жанры.
         О том, как использовать SQL оператор условия IN для получения по коллекции идентификаторов - можно посмотреть тут:
         https://www.baeldung.com/spring-jdbctemplate-in-list
         Таким образом мы избавимся от n-лишних запросов к БД и улучшим производительность приложения)
         **/
        Set<Genre> genres = new LinkedHashSet<>();
        SqlRowSet genreRowSet = jdbcTemplate.queryForRowSet("SELECT g.ID, g.GENRE " +
                "FROM GENRE AS g " +
                "INNER JOIN GENRE_FILM AS gf ON g.ID = gf.GENRE_ID " +
                "WHERE FILM_ID = ? " +
                "GROUP BY g.ID, g.GENRE " +
                "ORDER BY g.ID", filmId);
        while (genreRowSet.next()) {
            genres.add(new Genre(genreRowSet.getInt("ID"), genreRowSet.getString("GENRE")));
        }
        film.setGenres(genres);
        return film;
    }

    private void getMaxIdFilm() {
        Integer filmIdDb = jdbcTemplate.queryForObject("SELECT MAX(FILM_ID) FROM FILM", Integer.class);
        filmId = Objects.requireNonNullElse(filmIdDb, 0);
    }

    private void butchUpdate(Film film) {
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
