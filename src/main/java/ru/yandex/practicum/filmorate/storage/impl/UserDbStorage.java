package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.user.IncorrectUserIdException;
import ru.yandex.practicum.filmorate.exception.user.UserDatabaseIsEmptyException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        String sql = "INSERT INTO USERS(NAME, LOGIN, EMAIL, BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        log.info("Добавлен пользователь {}", user.getName());
        return user;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("UPDATE USERS SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?",
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        log.info("Данные пользователя {} успешно обновлены", user.getName());
        return user;
    }

    @Override
    public List<User> getAll() {
        SqlRowSet userRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM USERS");
        List<User> allUsersDb = new ArrayList<>();
        while (userRowSet.next()) {
            allUsersDb.add(makeUser(userRowSet));
        }
        if (allUsersDb.isEmpty()) {
            log.error("В базе не сохранено ни одного пользователя");
            throw new UserDatabaseIsEmptyException("В базе не сохранено ни одного пользователя");
        }
        log.info("Запрошены все пользователи из базы данных");
        return allUsersDb;
    }

    @Override
    public User getById(Integer id) {
        SqlRowSet userRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", id);
        userRowSet.next();
        if (userRowSet.last()) {
            log.info("Запрошен пользователь с id={}", id);
            return makeUser(userRowSet);
        } else {
            log.error("Указан некорректный id пользователя");
            throw new IncorrectUserIdException("Указан некорректный id пользователя");
        }
    }

    private User makeUser(SqlRowSet userRowSet) {
        User user = new User();
        user.setId(userRowSet.getInt("USER_ID"));
        user.setName(userRowSet.getString("NAME"));
        user.setLogin(userRowSet.getString("LOGIN"));
        user.setEmail(userRowSet.getString("EMAIL"));
        user.setBirthday(Objects.requireNonNull(userRowSet.getDate("BIRTHDAY")).toLocalDate());
        return user;
    }
}