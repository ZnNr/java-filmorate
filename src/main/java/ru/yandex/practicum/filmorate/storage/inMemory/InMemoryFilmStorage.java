package ru.yandex.practicum.filmorate.storage.inMemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorageInMemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorageInMemory {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private int idGen = 1;

    @Autowired
    public InMemoryFilmStorage() {
    }

    //Добавляем новый фильм в коллекцию
    @Override
    public Film addFilm(Film film) {
        film.setId(idGen++);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    //Обновляем фильм в коллекции, проверив его наличие
    @Override
    public Film updateFilm(Film film) {
        getFilm(film.getId());
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
    public Film getFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Такого фильма не существует!, {}", filmId);
            throw new NotFoundException("Такого фильма не существует!");
        }
        return films.get(filmId);
    }


}