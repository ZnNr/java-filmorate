package ru.yandex.practicum.filmorate.storage.dao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.LikeStorage;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikesDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int filmId, int userId) {
        Like like = Like.builder()
                .filmId(filmId)
                .userId(userId)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("likes");
        simpleJdbcInsert.execute(toMap(like));
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public HashSet<Integer> getListOfLikes(int filmId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId));
    }

    @Override
    public HashSet<Integer> getTheBestFilms(int count) {
        String sqlQuery = "SELECT films.film_id " +
                "FROM films " +
                "LEFT OUTER JOIN likes ON films.film_id = likes.film_id " +
                "GROUP BY films.film_id " +
                "ORDER BY COUNT(DISTINCT likes.user_id) DESC " +
                "LIMIT + ?";
        return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Integer.class, count));
    }

    private Map<String, Object> toMap(Like likes) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_Id", likes.getUserId());
        values.put("film_Id", likes.getFilmId());
        return values;
    }
}