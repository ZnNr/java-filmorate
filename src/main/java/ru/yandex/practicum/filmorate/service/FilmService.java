package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.parse("1895-12-28");

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public Film addFilm(Film film) {
        validate(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        return filmStorage.updateFilm(film);
    }


    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    //Проверка добавления нового фильма в соответствии с требованиями
    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация не пройдена: отсутствует название фильма.");
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Валидация не пройдена: описание превышает 200 символов.");
            throw new ValidationException("Описание превышает 200 символов.");
        }
        if (film.getDuration() < 0) {
            log.warn("Валидация не пройдена: отрицательная продолжительность фильма.");
            throw new ValidationException("Продолжительность фильма не может быть отрицательным.");
        }
        if (filmStorage.getFilms().contains(film)) {
            log.error("Такой фильм уже есть!, {}", film);
            throw new ValidationException("Такой фильм уже есть!");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Валидация не пройдена: дата релиза раньше {}", MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза фильма должна быть после " + MIN_RELEASE_DATE);
        }

    }

    public Film addLike(Integer filmId, Integer userId) {
        Film currentFilm = filmStorage.getFilm(filmId);
        userStorage.getUserById(userId);
        currentFilm.getLikes().add(userId);
        log.info("Пользователь с id:" + userId + " поставил лайк фильму с id:" + filmId);
        return currentFilm;
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film currentFilm = filmStorage.getFilm(filmId);
        userStorage.getUserById(userId);
        if (!currentFilm.getLikes().contains(userId)) {
            log.error("У фильма с id" + filmId + " Нет лайка от пользователя с id:" + userId);
            throw new NotFoundException("У фильма нет лайка от этого пользователя");
        }
        currentFilm.getLikes().remove(userId);
        log.info("Пользователь с id:" + userId + " удалил свой лайк фильму с id:" + filmId);
        return currentFilm;
    }

    //Получаем коллекцию самых популярных фильмов
    public List<Film> getMostLikedFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film f0, Film f1) {
        return Integer.compare(f1.getLikes().size(), f0.getLikes().size());
    }


}