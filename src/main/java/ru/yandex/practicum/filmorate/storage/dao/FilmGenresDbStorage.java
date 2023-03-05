package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmGenresStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@Slf4j
public class FilmGenresDbStorage implements FilmGenresStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addGenres(List<Genre> genres, int filmId) {
        String sqlQuery = "INSERT INTO film_genre_line (film_id, genre_id) VALUES (?, ?)";
        List<Genre> uniqueGenres = genres.stream()
                .distinct()
                .collect(Collectors.toList());
        getJdbcTemplate().batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                Genre genre = uniqueGenres.get(i);
                ps.setInt(1, filmId);
                ps.setInt(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return uniqueGenres.size();
            }
        });
    }

    @Override
    public void deleteGenres(int filmId) {
        String sqlQuery = "DELETE FROM film_genre_line WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Integer> getListOfGenres(int id) {
        String sqlQuery = "SELECT genre_id FROM film_genre_line WHERE film_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, id);
    }

    private JdbcOperations getJdbcTemplate() {
        return jdbcTemplate;
    }
}

