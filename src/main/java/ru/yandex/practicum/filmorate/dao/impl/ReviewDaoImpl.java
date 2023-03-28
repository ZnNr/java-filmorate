package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Component
public class ReviewDaoImpl implements ReviewDao {

    private static final String SQL_ADD_REVIEW = "INSERT INTO review(content, is_positive, film_id, user_id) VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE_REVIEW = "UPDATE review SET content = ?, is_positive = ? WHERE review_id = ?";

    private static final String SQL_DELETE_REVIEW = "DELETE FROM review WHERE review_id = ?";

    private static final String SQL_FIND_REVIEW_BY_ID = "SELECT r.* ," +
            "SUM(CASE WHEN rr.rate = 1 THEN 1 ELSE 0 END) - " +
            "SUM(CASE WHEN rr.rate = -1 THEN 1 ELSE 0 END) AS useful " +
            "FROM review r " +
            "LEFT JOIN review_rate rr ON r.review_id = rr.review_id " +
            "WHERE r.review_id = ? " +
            "GROUP BY r.review_id ";


    private static final String SQL_FIND_REVIEW_BY_FILM_ID = "SELECT r.* ," +
            "SUM(CASE WHEN rr.rate = 1 THEN 1 ELSE 0 END) - " +
            "SUM(CASE WHEN rr.rate = -1 THEN 1 ELSE 0 END) AS useful " +
            "FROM review r " +
            "LEFT JOIN review_rate rr ON r.review_id = rr.review_id " +
            "WHERE r.film_id = ? " +
            "GROUP BY r.review_id " +
            "ORDER BY useful DESC " +
            "LIMIT ?";

    private static final String SQL_FIND_REVIEWS = "SELECT r.*, " +
            "SUM(CASE WHEN rr.rate = 1 THEN 1 ELSE 0 END) - " +
            "SUM(CASE WHEN rr.rate = -1 THEN 1 ELSE 0 END) AS useful " +
            "FROM review r " +
            "LEFT JOIN review_rate rr ON r.review_id = rr.review_id " +
            "GROUP BY r.review_id " +
            "ORDER BY useful DESC " +
            "LIMIT ?";


    private final JdbcTemplate jdbcTemplate;


    @Override
    public Review add(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_ADD_REVIEW, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getFilmId());
            stmt.setLong(4, review.getUserId());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        log.debug("Добавлено новое ревью c ID={}.", review.getReviewId());

        return review;
    }

    @Override
    public void deleteById(Long reviewId) {
        log.debug("Улделено ревью с ID={}.", reviewId);
        jdbcTemplate.update(SQL_DELETE_REVIEW, reviewId);
    }

    @Override
    public Review update(Review review) {
        int check = jdbcTemplate.update(SQL_UPDATE_REVIEW, review.getContent(), review.getIsPositive(), review.getReviewId());

        if (check == 0) {
            throw new ElementNotFoundException(String.format("Отзыв с ID=%d не существует", review.getReviewId()));
        }
        log.debug("Обновленно ревью с ID={}.", review.getReviewId());
        return findById(review.getReviewId());
    }

    @Override
    public Review findById(Long reviewId) {
        log.debug("Иницилизирован поиск ревью по ID={}.", reviewId);
        return jdbcTemplate.query(SQL_FIND_REVIEW_BY_ID, this::makeReview, reviewId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ElementNotFoundException(String.format(
                        "Отзыв с ID=%d не найден!", reviewId)));
    }


    @Override
    public List<Review> findAllReviews(Integer limit) {
        log.debug("Иницилизирован поиск всех ревью.");
        return jdbcTemplate.query(SQL_FIND_REVIEWS, this::makeReview, limit);
    }

    @Override
    public List<Review> findReviewsByFilmId(Integer limit, Long filmId) {
        log.debug("Иницилизирован поиск ревью с лимитом={} по фильму с ID={}",limit, filmId);
        return jdbcTemplate.query(SQL_FIND_REVIEW_BY_FILM_ID, this::makeReview, filmId, limit);
    }

    private Review makeReview(ResultSet resultSet, int rowNum) throws SQLException {
        return new Review(
                resultSet.getLong("review_id"),
                resultSet.getString("content"),
                resultSet.getBoolean("is_positive"),
                resultSet.getLong("user_id"),
                resultSet.getLong("film_id"),
                resultSet.getInt("useful"));
    }
}
