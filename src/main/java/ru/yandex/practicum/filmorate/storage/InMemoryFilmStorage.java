package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
        } else {
            log.warn("Не найден фильм при попытке обновления.");
            throw new NotFoundException("Фильм с ID " + film.getId() + " не существует.");
        }
        log.info("Обновлен фильм с ID {}", film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> filmList = new ArrayList<>();
        for (Map.Entry<Integer, Film> entry : films.entrySet()) {
            filmList.add(entry.getValue());
        }
        return filmList;
    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.warn("Не найден фильм при поиске по ID.");
            throw new NotFoundException("Фильм с ID: " + id + " не существует.");
        }
    }


    @Override
    public Film deleteFilm(int id) {
        return films.remove(id);
    }


    @Override
    public void addLike(Film film, int userId) {
        film.getLikes().add(userId);
    }

    @Override
    public void deleteLike(Film film, int userId) {
        film.getLikes().remove(userId);
    }


}