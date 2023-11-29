package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Override
    public User add(User user) {
        userNameValidation(user); // проверка имени пользователя
        return userStorage.add(user);
    }

    @Override
    public User update(User user) {
        userValidation(user.getId());
        userNameValidation(user); // проверка имени пользователя
        return userStorage.update(user);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userStorage.getAll());
    }

    @Override
    public User getById(Integer id) {
        userValidation(id);
        return userStorage.getById(id);
    }

    @Override
    public User addToFriends(Integer id, Integer friendId) {
        userValidation(id);
        userValidation(friendId);
        friendStorage.add(id, friendId);
        return getById(id);
    }

    @Override
    public User removeFriend(Integer id, Integer friendId) {
        userValidation(id);
        userValidation(friendId);
        friendStorage.remove(id, friendId);
        return getById(id);
    }

    @Override
    public List<User> getFriendsById(Integer id) {
        userValidation(id);
        return friendStorage.getFriendsById(id);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer otherId) { // общие друзья
        userValidation(id);
        userValidation(otherId);
        return friendStorage.getCommonFriends(id, otherId);
    }

    private void userValidation(Integer id) {
        if (userStorage.getById(id) == null) {
            throw new UserValidationException(String.format("Пользователь с id=%s не найден в базе", id));
        }
    }

    private void userNameValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
