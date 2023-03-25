package ru.yandex.practicum.filmorate.dao;


public interface ReviewRateDao {

    void addDislike(Long revId, Long userId);

    void addLike(Long revId, Long userId);

    void deleteLikeOrDislike(Long revId, Long userId);
}
