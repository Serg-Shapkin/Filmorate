package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.user.IncorrectUserIdException;
import ru.yandex.practicum.filmorate.exception.user.UserDatabaseIsEmptyException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private int userId = 0;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        makeUserId();
    }

    @Override
    public User add(User user) {
        userId++;
        user.setId(userId);
        jdbcTemplate.update("INSERT INTO USERS VALUES (?,?,?,?,?)",
                user.getId(),
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday());
        SqlRowSet userRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", user.getId());
        userRowSet.next();
        log.info("Пользователь {} успешно добавлен", user.getName());
        return makeUser(userRowSet);
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("UPDATE USERS SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ? WHERE USER_ID = ?",
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());

        SqlRowSet userRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM USERS WHERE USER_ID = ?", user.getId());
        if (userRowSet.next()) {
            log.info("Данные пользователя {} успешно обновлены", user.getName());
            return makeUser(userRowSet);
        } else {
            return null;
        }
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

        SqlRowSet userRowSetFriends = jdbcTemplate.queryForRowSet("SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?",
                userRowSet.getInt("USER_ID"));

        Set<Integer> friends = new HashSet<>();
        while (userRowSetFriends.next()) {
            friends.add(userRowSetFriends.getInt("FRIEND_ID"));
        }
        return user;
    }

    private void makeUserId() { // если в бд есть пользователи, то запоминаем максимальный id
        Integer userIdDb = jdbcTemplate.queryForObject("SELECT MAX(USER_ID) FROM USERS", Integer.class);
        userId = Objects.requireNonNullElse(userIdDb, 0);
    }
}