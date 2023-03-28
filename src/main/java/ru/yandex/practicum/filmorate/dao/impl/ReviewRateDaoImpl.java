package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewRateDao;

@Slf4j
@AllArgsConstructor
@Component
public class ReviewRateDaoImpl implements ReviewRateDao {

    private static final String SQL_ADD_LIKE_REVIEW =
            "INSERT INTO review_rate(review_id, user_id, rate) " +
            "VALUES (?, ?, 1)";

    private static final String SQL_ADD_DISLIKE_REVIEW =
            "INSERT INTO review_rate(review_id, user_id, rate) " +
                    "VALUES (?, ?, -1)";

    private static final String SQL_DELETE_REVIEW_RATE = "DELETE FROM review_rate WHERE review_id = ? AND user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Long revId, Long userId) {
        log.debug("Пользоватвель с ID={} поставил лайк ревью с ID={}.", userId, revId);
        jdbcTemplate.update(SQL_ADD_LIKE_REVIEW, revId, userId);
    }

    @Override
    public void addDislike(Long revId, Long userId) {
        log.debug("Пользоватвель с ID={} поставил дизлайк ревью с ID={}.", userId, revId);

        jdbcTemplate.update(SQL_ADD_DISLIKE_REVIEW, revId, userId);
    }

    public void deleteLikeOrDislike(Long revId, Long userId) {
        log.debug("Пользователь c ID={} удалил лайк или дизлайк ревью с ID={}", userId, revId);
        jdbcTemplate.update(SQL_DELETE_REVIEW_RATE, revId, userId);
    }
}
