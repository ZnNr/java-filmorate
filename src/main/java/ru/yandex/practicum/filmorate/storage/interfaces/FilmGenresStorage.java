package ru.yandex.practicum.filmorate.storage.interfaces;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
public interface FilmGenresStorage {

    void addGenres(List<Genre> genres, int filmId);

    void deleteGenres(int filmId);

    List<Integer> getListOfGenres(int genreId);
}
