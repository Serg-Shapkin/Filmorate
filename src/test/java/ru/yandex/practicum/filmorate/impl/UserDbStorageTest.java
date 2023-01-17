package ru.yandex.practicum.filmorate.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional // помогает откатиться назад после завершения теста.
public class UserDbStorageTest {
    private final UserStorage userStorage;

    @Test
    @DisplayName("Проверка добавления пользователя")
    public void testAddUser() {
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
        userStorage.add(user1);

        final int userId = user1.getId();
        final User savedUser = userStorage.getById(userId);

        assertNotNull(user1, "Пользователь не найден");
        assertEquals(user1, savedUser, "Пользователи не совпадают");

        final List<User> users = new ArrayList<>(userStorage.getAll());

        assertNotNull(users, "Пользователи не возвращаются");
        assertEquals(1, users.size(), "Неверное количество пользователей");
        assertEquals(user1, users.get(0), "Пользователи не совпадают");
    }

    @Test
    @DisplayName("Проверка обновления данных о пользователе")
    void testUpdateUser() {
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
        userStorage.add(user1);

        final int userId = user1.getId();
        User savedUser = userStorage.getById(userId);

        User newUser = new User(1, "Oleg", "OlegIvanov", "olegivanov@ya.ru", LocalDate.of(1987, 5, 25));
        newUser.setId(savedUser.getId());

        savedUser = newUser;
        userStorage.update(savedUser);

        assertNotNull(savedUser, "Пользователь не найден");
        assertEquals(newUser, savedUser, "Пользователи не совпадают");

        final List<User> users = new ArrayList<>(userStorage.getAll());
        assertNotNull(users, "Пользователи не возвращаются");
        assertEquals(1, users.size(),"Неверное количество пользователей");
        assertEquals(newUser, users.get(0),"Пользователи не совпадают");
    }

    @Test
    @DisplayName("Проверка получения всех пользователей")
    void testGetAllUsers() {
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
        User user2 = new User(2, "Oleg", "OlegIvanov", "olegivanov@ya.ru", LocalDate.of(1987, 5, 25));

        userStorage.add(user1);
        userStorage.add(user2);

        final List<User> users = new ArrayList<>(userStorage.getAll());

        assertNotNull(users, "Пользователи не возвращаются");
        assertEquals(2, users.size(),"Неверное количество пользователей");
        assertEquals(user1, users.get(0),"Пользователи не совпадают");
    }

    @Test
    @DisplayName("Проверка получения пользователя по id")
    void testGetUserById() {
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
        userStorage.add(user1);

        final int userId = user1.getId();
        final User savedUser = userStorage.getById(userId);

        assertNotNull(savedUser, "Пользователь не найден");
        assertEquals(userId, user1.getId(), "Id пользователей не совпадают");
    }
}
