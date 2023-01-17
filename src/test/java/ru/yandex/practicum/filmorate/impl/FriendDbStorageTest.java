package ru.yandex.practicum.filmorate.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
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

public class FriendDbStorageTest {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;


    @Test
    @DisplayName("Проверка добавления в друзья")
    void testAddToFriends() {
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
        User user2 = new User(2, "Oleg", "OlegIvanov", "olegivanov@ya.ru", LocalDate.of(1987, 5, 25));

        userStorage.add(user1);
        userStorage.add(user2);

        friendStorage.add(user1.getId(), user2.getId()); // первый добавил второго

        final List<User> friends = new ArrayList<>(friendStorage.getFriendsById(user1.getId()));

        assertNotNull(friends, "Друзья не возвращаются");
        assertEquals(1, friends.size(),"Неверное количество друзей");
        assertEquals(user2, friends.get(0),"Добавлен какой то другой друг =/");
    }

    @Test
    @DisplayName("Проверка удаления из друзей")
    void testRemoveFriends() {
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
        User user2 = new User(2, "Oleg", "OlegIvanov", "olegivanov@ya.ru", LocalDate.of(1987, 5, 25));

        userStorage.add(user1);
        userStorage.add(user2);

        friendStorage.add(user1.getId(), user2.getId()); // первый добавил второго
        friendStorage.remove(user1.getId(), user2.getId()); // первый удалил второго

        final List<User> friends = new ArrayList<>(friendStorage.getFriendsById(user1.getId()));

        assertNotNull(friends, "Друзья не возвращаются");
        assertEquals(0, friends.size(),"Неверное количество друзей");
    }

    @Test
    @DisplayName("Проверка получения друзей по id") // тоже что и addToFriends
    void testGetFriendsById() {
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
        User user2 = new User(2, "Oleg", "OlegIvanov", "olegivanov@ya.ru", LocalDate.of(1987, 5, 25));

        userStorage.add(user1);
        userStorage.add(user2);

        friendStorage.add(user1.getId(), user2.getId()); // первый добавил второго

        final List<User> friends = new ArrayList<>(friendStorage.getFriendsById(user1.getId()));

        assertNotNull(friends, "Друзья не возвращаются");
        assertEquals(1, friends.size(),"Неверное количество друзей");
        assertEquals(user2, friends.get(0),"Добавлен какой то другой друг =/");
    }

    @Test
    @DisplayName("Проверка получения общих друзей")
    public void testGetCommonFriends() {
        User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
        User user2 = new User(2, "Oleg", "OlegIvanov", "olegivanov@ya.ru", LocalDate.of(1987, 5, 25));
        User user3 = new User(3, "Sveta", "SvetaSvetlanova", "svetasvetlanova@ya.ru", LocalDate.of(1992, 8, 12));

        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.add(user3);

        friendStorage.add(user1.getId(), user3.getId()); // первый добавил третьего
        friendStorage.add(user2.getId(), user3.getId()); // второй добавил третьего

        List<User> commonFriends = new ArrayList<>(friendStorage.getCommonFriends(user1.getId(), user2.getId()));

        assertNotNull(commonFriends, "Друзья не возвращаются");
        assertEquals(1, commonFriends.size(),"Неверное количество общих друзей");
        assertEquals(user3, commonFriends.get(0),"Друзья не совпадают");
    }
}
