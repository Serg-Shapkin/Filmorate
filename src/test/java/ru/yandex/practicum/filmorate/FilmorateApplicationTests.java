package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional // помогает откатиться назад после завершения теста.
class FilmorateApplicationTests {
	private final UserStorage userStorage;
	private final FriendStorage friendStorage;
	private final FilmStorage filmStorage;
	private final LikeStorage likeStorage;
	private final JdbcTemplate jdbcTemplate;

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

	@Test
	@DisplayName("Проверка добавления фильма")
	public void testAddFilm() {
		Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1,15),90, 1, new Rating(2, "PG"), new LinkedHashSet<>());
		filmStorage.add(film1);

		final int filmId = film1.getId();
		final Film savedFilm = filmStorage.getById(filmId);

		assertNotNull(film1, "Фильм не найден");
		assertEquals(film1, savedFilm, "Фильмы не совпадают");

		final List<Film> films = new ArrayList<>(filmStorage.getAll());

		assertNotNull(films, "Фильмы не возвращаются");
		assertEquals(1, films.size(), "Неверное количество фильмов");
		assertEquals(film1, films.get(0), "Фильмы не совпадают");
	}

	@Test
	@DisplayName("Проверка обновления данных о фильме")
	void testUpdateFilm() {
		Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1,15),90, 1, new Rating(2, "PG"), new LinkedHashSet<>());
		filmStorage.add(film1);

		final int filmId = film1.getId();
		Film savedFilm = filmStorage.getById(filmId);

		Film newFilm = new Film(1, "NewFilm", "Description_New", LocalDate.of(2022, 2,25),90, 1, new Rating(2, "PG"), new LinkedHashSet<>());
		newFilm.setId(savedFilm.getId());

		savedFilm = newFilm;
		filmStorage.update(savedFilm);

		assertNotNull(savedFilm, "Фильм не найден");
		assertEquals(newFilm, savedFilm, "Фильмы не совпадают");

		final List<Film> films = new ArrayList<>(filmStorage.getAll());
		assertNotNull(films, "Фильмы не возвращаются");
		assertEquals(1, films.size(),"Неверное количество фильмов");
		//assertEquals(newFilm, films.get(0),"Фильмы не совпадают");
	}

	@Test
	@DisplayName("Проверка получения всех фильмов")
	void testGetAllFilms() {
		Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1,15),90, 1, new Rating(2, "PG"), new LinkedHashSet<>());
		Film film2 = new Film(2, "TestFilm2", "Description_2", LocalDate.of(2022, 2,25),90, 1, new Rating(2, "PG"), new LinkedHashSet<>());

		filmStorage.add(film1);
		filmStorage.add(film2);

		final List<Film> films = new ArrayList<>(filmStorage.getAll());

		assertNotNull(films, "Фильмы не возвращаются");
		assertEquals(2, films.size(), "Неверное количество фильмов");
		assertEquals(film1, films.get(0), "Фильмы не совпадают");
	}

	@Test
	@DisplayName("Проверка получения фильма по id")
	void TestGetFilmById() {
		Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1,15),90, 1, new Rating(2, "PG"), new LinkedHashSet<>());
		filmStorage.add(film1);

		final int filmId = film1.getId();
		final Film savedFilm = filmStorage.getById(filmId);

		assertNotNull(savedFilm, "Фильм не найден");
		assertEquals(filmId, film1.getId(), "Id фильмов не совпадают");
	}

/*	@Test
	@DisplayName("Проверка постановки лайка фильму")
	public void testAddLikeFilm() {

		Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1,15),90, 1, new Rating(2, "PG"), new LinkedHashSet<>());
		filmStorage.add(film1);

		User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
		userStorage.add(user1);

		likeStorage.add(film1.getId(), user1.getId()); // какому фильму, кто поставил

		final List<Integer> likes = new ArrayList<>(filmStorage.getById(film1.getId()).getLikes());

		assertNotNull(likes, "Лайки не возвращаются");
		assertEquals(1, likes.size(),"Неверное количество лайков");
		assertEquals(user1.getId(), likes.get(0),"Добавлен лайк другим пользователем =/");
	}

	@Test
	@DisplayName("Проверка удаления лайка фильму")
	public void testRemoveLikeFilm() {

		Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1,15),90, 1, new Rating(2, "PG"), new LinkedHashSet<>());
		filmStorage.add(film1);

		User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
		userStorage.add(user1);

		likeStorage.add(film1.getId(), user1.getId()); // какому фильму, кто поставил
		likeStorage.remove(film1.getId(), user1.getId()); // какому фильму, кто удалил

		final List<Integer> likes = new ArrayList<>(filmStorage.getById(film1.getId()).getLikes());

		assertNotNull(likes, "Лайки не возвращаются");
		assertEquals(0, likes.size(),"Неверное количество лайков");
	}

	@Test
	@DisplayName("Проверка получения популярных фильмов")
	public void testGetPopularFilms() {
		User user1 = new User(1, "Ivan", "IvanIvanov", "ivanivanov@ya.ru", LocalDate.of(2000, 10, 5));
		User user2 = new User(2, "Oleg", "OlegIvanov", "olegivanov@ya.ru", LocalDate.of(1987, 5, 25));

		userStorage.add(user1);
		userStorage.add(user2);

		Film film1 = new Film(1, "TestFilm1", "Description_1", LocalDate.of(2022, 1,15),90, 1, new Rating(2, "PG"), new LinkedHashSet<>());
		Film film2 = new Film(2, "TestFilm2", "Description_2", LocalDate.of(2022, 2,25),90,  1, new Rating(2, "PG"), new LinkedHashSet<>());

		filmStorage.add(film1);
		filmStorage.add(film2);

		// 1 лайк у film1
		likeStorage.add(film1.getId(), user1.getId()); // лайк film1  от user1

		// 2 лайка у film2
		likeStorage.add(film2.getId(), user1.getId());
		likeStorage.add(film2.getId(), user2.getId());

		final List<Film> popularFilm = new ArrayList<>(filmStorage.getPopular(2));

		assertEquals(2, popularFilm.get(0).getLikes().size(),"Это не самый популярный фильм");
	}*/
}
