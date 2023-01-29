package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private int id = 1;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.parse("1895-12-28");
    private final Map<Integer, Film> filmsMap = new HashMap<>();

    @PostMapping
    public Film addNewFilm(@Valid @RequestBody Film film) {
        log.info("Добавление фильма");
        validateFilm(film);

        film.setId(id++);
        filmsMap.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновление фильма");
        validateFilm(film);
        if (!filmsMap.containsKey(film.getId())) {
            log.warn("Фильм с таким id не существует");
            throw new ValidationException("Фильм с таким id не существует");
        }

        filmsMap.replace(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение списка всех фильмов");
        List<Film> filmList = new ArrayList<>();
        for (Map.Entry<Integer, Film> entry : filmsMap.entrySet()) {
            filmList.add(entry.getValue());
        }
        return filmList;
    }


    private void validateFilm(Film film) {
        if (film == null) {
            log.warn("Ошибка: фильм не может быть null");
            throw new ValidationException("Ошибка: фильм не может быть null");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка: название не может быть пустым");
            throw new ValidationException("Ошибка: название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Ошибка: Максимальная длина описания - 200 символов");
            throw new ValidationException("Ошибка: Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Ошибка: Дата релиза не должно быть раньше {}", MIN_RELEASE_DATE);
            throw new ValidationException("Ошибка: Дата релиза фильма должна быть после " + MIN_RELEASE_DATE);
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка:Продолжительность фильма не может быть меньше нуля");
            throw new ValidationException("Ошибка:Продолжительность фильма не может быть меньше нуля");
        }
    }

}