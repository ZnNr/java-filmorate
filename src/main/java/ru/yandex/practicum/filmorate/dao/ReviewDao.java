package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {

    Review add(Review review);

    Review update(Review review);

    void deleteById(Long reviewId);

    Review findById(Long reviewId);

    List<Review> findAllReviews(Integer limit);

    List<Review> findReviewsByFilmId(Integer count, Long filmId);
}
