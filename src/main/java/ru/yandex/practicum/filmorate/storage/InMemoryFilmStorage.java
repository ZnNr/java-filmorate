package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> filmMap = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        filmMap.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmMap.containsKey(film.getId())) {
            filmMap.put(film.getId(), film);
        } else {
            log.warn("Не найден фильм при попытке обновления.");
            throw new UserNotFoundException("Фильм с ID " + film.getId() + " не существует.");
        }
        log.info("Обновлен фильм с ID {}", film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> filmList = new ArrayList<>();
        for (Map.Entry<Integer, Film> entry : filmMap.entrySet()) {
            filmList.add(entry.getValue());
        }
        return filmList;
    }

    @Override
    public Film getFilmById(int id) {
        if (filmMap.containsKey(id)) {
            return filmMap.get(id);
        } else {
            log.warn("Не найден фильм при поиске по ID.");
            throw new FilmNotFoundException("Фильм с ID: " + id + " не существует.");
        }
    }
}