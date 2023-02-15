package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.parse("1895-12-28");
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private int idGen = 1;

    @Autowired
    public InMemoryFilmStorage() {
    }

    //Добавляем новый фильм в коллекцию
    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        film.setId(idGen++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    //Обновляем фильм в коллекции, проверив его наличие
    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        films.put(film.getId(), film);
        log.info("Фильм обновлен - , {}");
        return film;
    }

    //Получаем список всех фильмов по запросу
    @Override
    public List<Film> getFilms() {
        List<Film> filmList = new ArrayList<>();
        for (Map.Entry<Integer, Film> entry : films.entrySet()) {
            filmList.add(entry.getValue());
        }
        return filmList;
    }

    //Получаем один фильм
    @Override
    public Film getFilmById(Integer filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Такого фильма не существует!, {}", filmId);
            throw new NotFoundException("Такого фильма не существует!");
        }
        return films.get(filmId);
    }


    //Проверка добавления нового фильма в соответствии с требованиями
    private void validateFilm(Film film) {
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
        if (getFilms().contains(film)) {
            log.error("Такой фильм уже есть!, {}", film);
            throw new ValidationException("Такой фильм уже есть!");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Валидация не пройдена: дата релиза раньше {}", MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза фильма должна быть после " + MIN_RELEASE_DATE);
        }

    }



}