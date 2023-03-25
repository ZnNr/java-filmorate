package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.constants.EventType;
import ru.yandex.practicum.filmorate.utility.constants.Operation;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikesDao likesDao;
    private final EventService eventService;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            LikesDao likesDao, EventService eventService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesDao = likesDao;
        this.eventService = eventService;
    }


    /**
     * Метод по добавлению фильма
     * @param film фильм который необходимо создать {@link FilmStorage#addFilm(Film)}
     * @return Возвращает фильм
     */
    public Film addFilm(Film film) {
        if (film.getGenres() != null && film.getGenres().size() > 1) {
            film.setGenres(film.getGenres()
                    .stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }
        return filmStorage.addFilm(film);
    }

    /**
     * Метод по модифицированию фильма {@link FilmStorage#updateFilm(Film)}
     * @param film фильм, который необходимо изменить
     * @return Возвращает измененный фильм
     */
    public Film updateFilm(Film film) {
        if (film.getGenres() != null && film.getGenres().size() > 1) {
            film.setGenres(film.getGenres()
                    .stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }


        return filmStorage.updateFilm(film);
    }

    /**
     * Метод позволяющий пользователем ставить лайки фильмам.
     * @param id фильм, которому ставиться лайк.
     * @param userId пользователь, который ставит лайк.
     */
    public void putLike(Long id, Long userId) {
        userStorage.findUserById(userId);
        filmStorage.findFilmById(id);

        likesDao.putLike(id, userId);
        eventService.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.LIKE, Operation.ADD, id));
    }

    /**
     * Метод по удалению лайка фильму пользователем.
     * @param id идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    public void deleteLike(Long id, Long userId) {
        userStorage.findUserById(userId);
        filmStorage.findFilmById(id);

        likesDao.removeLike(id, userId);
        eventService.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.LIKE, Operation.REMOVE, id));
    }

    /**
     * Метод по возвращению всех фильмов.
     * @return Возвращает все фильмы. {@link FilmStorage#findAllFilms()}
     */
    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    /**
     * Метод по нахождению фильма по его id.
     * @param id идентификатор фильма.
     * @return Возвращает фильм по его id. {@link FilmStorage#findFilmById(Long)}
     */
    public Film findFilmById(Long id) {
        return filmStorage.findFilmById(id);
    }

    /**
     * Метод по нахождению популярных по количеству лайков фильмов.
     * @param count количество фильмов, по умолчанию 10.
     * @return Возвращает список популярных фильмов согласна параметру count.
     */
    public List<Film> findPopularFilms(Integer count, Integer genreId, Integer year) {
        if (genreId != -1 || year != -1) {
            return filmStorage.getPopularFilmByDateAndGenre(count, genreId, year);
        } else {
            return filmStorage.getPopularFilm(count);
        }
    }

    public List<Film> findByParameter(String query, String by) {
        return filmStorage.findByParameter(query, by);
    }

    public List<Film> findFilmBySorting(Long directorId, String sortBy) {
        return filmStorage.findFilmBySorting(directorId, sortBy);
    }

    public void deleteFilmById(Long id) {
        filmStorage.deleteFilmById(id);
    }



    /**
     * Метод нахождения общих фильмов у друзей.
     * @param friendId идентификатор друга.
     * @param userId идентификатор пользователя.
     */
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return filmStorage.getCommonFilms(userStorage.findUserById(userId).getId(),
                userStorage.findUserById(friendId).getId());
    }
}


