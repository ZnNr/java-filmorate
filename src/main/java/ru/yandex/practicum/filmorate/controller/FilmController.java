package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;


    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") @Positive int filmId) {
        return filmService.getFilm(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getMostLikedFilms(@RequestParam(defaultValue = "10", required = false) @Positive Integer count) {
        return filmService.getMostLikedFilms(count);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") @Positive int filmId,
                        @PathVariable("userId") @Positive Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") @Positive int filmId,
                           @PathVariable("userId") @Positive Integer userId) {
        filmService.deleteLike(filmId, userId);
    }


}