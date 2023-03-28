package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

/**
 * Интерфес для работы с рекомендацииями из БД
 */
public interface RecommendationDao {

    /**
     * Получения айди самого большого по лайкам фильмов с юзером
     * @param id - айди юзера
     * @return - айди другого юзера
     */
    Optional<Long> getIdCommonFilmWithCurrentId(long id);

    /**
     * Получение списка айди залайканых фильмов от юзера
     * @param id - айди юзера
     * @return - список айди фильмов
     */
    List<Long> getLikedFilmWithoutLiked(Optional<Long> id, Optional<Long> idOther);

    List<Film> getListFilm(List<Long> idFilms);
}