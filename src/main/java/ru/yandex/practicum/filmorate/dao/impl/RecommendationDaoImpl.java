package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.RecommendationDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Класс, реализующий методы интрерфейса RecommendationDao
 */
@Component
@Slf4j
public class RecommendationDaoImpl implements RecommendationDao {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;

    @Autowired
    public RecommendationDaoImpl(JdbcTemplate jdbcTemplate, FilmDbStorage filmDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmDbStorage = filmDbStorage;
    }

    @Override
    public Optional<Long> getIdCommonFilmWithCurrentId(long id) {
        String sqlRequest = "SELECT lf1.user_id\n" +
                "FROM like_to_film AS lf\n" +
                "         INNER JOIN like_to_film AS lf1 ON lf.film_id = lf1.film_id\n" +
                "WHERE lf.user_id = ? " +
                "AND NOT lf.USER_ID = lf1.USER_ID\n" +
                "GROUP BY lf1.user_id\n" +
                "ORDER BY COUNT(lf1.film_id) DESC;\n";
        return jdbcTemplate.query(sqlRequest, (rs, rowNum) -> rs.getLong("user_id"), id)
                .stream()
                .findFirst();
    }

    @Override
    public List<Long> getLikedFilmWithoutLiked(Optional<Long> id, Optional<Long> idOther) {
        String sqlRequest = "SELECT film_id\n" +
                "FROM LIKE_TO_FILM\n" +
                "WHERE USER_ID = ?\n" +
                "  AND FILM_ID NOT IN (SELECT FILM_ID\n" +
                "                      FROM LIKE_TO_FILM\n" +
                "                      WHERE USER_ID = ?)";
        return jdbcTemplate.query(sqlRequest, (rs, rowNum) -> rs.getLong("film_id"), idOther.get(), id.get());
    }

    @Override
    public List<Film> getListFilm(List<Long> idFilms) {
        String sqlRequest = "SELECT *\n" +
                "FROM FILM\n" +
                "WHERE FILM_ID IN(" + idFilms.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",","","")) + ")";
        return jdbcTemplate.query(sqlRequest, filmDbStorage::makeFilm);
    }

}