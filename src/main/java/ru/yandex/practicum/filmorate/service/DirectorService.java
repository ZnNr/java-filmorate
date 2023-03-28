package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDirectorDao;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DirectorService {

    private final FilmDirectorDao filmDirectorDao;

    public Director addDirector(Director director) {
        return filmDirectorDao.addDirector(director);
    }

    public void deleteDirector(Long directorId) {
        filmDirectorDao.deleteDirector(directorId);
    }

    public Director updateDirector(Director director) {
        return filmDirectorDao.updateDirector(director);
    }

    public Director findDirectorById(Long directorId) {
        return filmDirectorDao.findDirectorById(directorId);
    }

    public List<Director> findAllDirectors() {
        return filmDirectorDao.findAllDirectors();
    }
}
