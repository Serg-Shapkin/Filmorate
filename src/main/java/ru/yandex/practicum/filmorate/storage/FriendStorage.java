package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    void add(Integer id, Integer friendId);
    void remove(Integer id, Integer friendId);
    List<User> getFriendsById(Integer id);
    List<User> getCommonFriends(Integer id, Integer otherId);
}
