package ru.yandex.practicum.filmorate.storage;

public interface LikeStorage {
    void add(Integer id, Integer userId);
    void remove(Integer id, Integer userId);
}
