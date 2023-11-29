package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Integer id, Integer friendId) {
        String sql = "INSERT INTO FRIENDSHIP(USER_ID, FRIEND_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, friendId);
        log.info("Пользователь с id={} добавил в друзья пользователя c id={}", id, friendId);
    }

    @Override
    public void remove(Integer id, Integer friendId) {
        String sql = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, id, friendId);
        log.info("Пользователь с id={} удалил из друзей пользователя c id={}", id, friendId);
    }

    @Override
    public List<User> getFriendsById(Integer id) {
        String sql = "SELECT u.* FROM FRIENDSHIP f JOIN USERS U on U.USER_ID = f.FRIEND_ID WHERE f.USER_ID = ?";
        log.info("Запрошен список друзей пользователя с id={}", id);
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> makeUser(resultSet), id);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer friendId) {
        String sql = "SELECT * FROM USERS u, FRIENDSHIP f, FRIENDSHIP o WHERE u.USER_ID = f.FRIEND_ID AND u.USER_ID = o.FRIEND_ID AND f.USER_ID = ? AND o.USER_ID = ?";
        log.info("Запрошен список общих друзей пользователя с id={} и пользователя с id={}", id, friendId);
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> makeUser(resultSet), id, friendId);
    }

    private User makeUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("USER_ID"));
        user.setName(resultSet.getString("NAME"));
        user.setLogin(resultSet.getString("LOGIN"));
        user.setEmail(resultSet.getString("EMAIL"));
        user.setBirthday(resultSet.getDate("BIRTHDAY").toLocalDate());
        return user;
    }
}
