package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.parse("1895-12-28");
    int id = 0;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        validate(film);
        film.setId(generateId());
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public void addLike(int userId, int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().add(user.getId());
        log.info("Пользователь с ID {} поставил лайк фильму {}", userId, filmId);
    }

    public void deleteLike(int userId, int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().remove(user.getId());
        log.info("Пользователь с ID {} убрал лайк с фильма {}", userId, filmId);
    }

    public List<Film> getTopRatedFilms(int size) {
        return filmStorage.getFilms()
                .stream()
                .sorted((f0, f1) -> f1.getLikes().size() - f0.getLikes().size())
                .limit(size)
                .collect(Collectors.toList());
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Валидация не пройдена: отсутствует название фильма.");
            throw new ValidationException("Название не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Валидация не пройдена: описание превышает 200 символов.");
            throw new ValidationException("Описание превышает 200 символов.");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Валидация не пройдена: дата релиза раньше {}", MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза фильма должна быть после " + MIN_RELEASE_DATE);
        }
        if (film.getDuration() < 0) {
            log.warn("Валидация не пройдена: отрицательная продолжительность фильма.");
            throw new ValidationException("Продолжительность фильма не может быть отрицательным.");
        }
    }

    private int generateId() {
        return ++id;
    }

}