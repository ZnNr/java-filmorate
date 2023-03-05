package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorageInMemory {

    Film getFilm(int id);

    List<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);
}
