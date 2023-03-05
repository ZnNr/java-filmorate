package ru.yandex.practicum.filmorate.storage.interfaces;

import java.util.HashSet;

public interface LikeStorage {

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);


    HashSet<Integer> getListOfLikes(int filmId);

    HashSet<Integer> getTheBestFilms(int count);
}
