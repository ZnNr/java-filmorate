package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmGenresStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;
    private final FilmGenresStorage filmGenresStorage;

    public Collection<Genre> getGenres() {
        return genreStorage.getGenres();
    }

    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getListOfGenres(int id) {

        return filmGenresStorage.getListOfGenres(id).stream()
                .map(genreStorage::getGenreById)
                .collect(Collectors.toList());
    }
}