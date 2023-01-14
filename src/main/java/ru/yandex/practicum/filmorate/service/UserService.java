package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User add(User user);
    User update(User user);
    List<User> getAll();
    User getById(Integer id);

    User addToFriends(Integer id, Integer friendId);
    User removeFriend(Integer id, Integer friendId);
    List<User> getFriendsById(Integer id);
    List<User> getCommonFriends(Integer id, Integer otherId);
}
