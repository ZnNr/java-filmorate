package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

     /**
     * Эндпоин для создания фильма {@link ru.yandex.practicum.filmorate.storage.FilmStorage#addFilm(Film)}
     * @param film фильм, который необходимо создать
     * @return В ответ возвращает созданный объект
     */
    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    /**
     * Эндпоинт по модифицированию фильма.
     * @param film фильм, который необходимо изменить.
     * @return В ответ возвращает измененный фильм. {@link FilmService#updateFilm(Film)}
     */
    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    /**
     * Эндпоинт позволяющий пользователем ставить лайки фильмам. {@link FilmService#putLike(Long, Long)}
     * @param id фильм, которому ставиться лайк.
     * @param userId пользователь, который ставит лайк.
     */
    @PutMapping("/{id}/like/{userId}")
    public void putLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.putLike(id, userId);
    }

    /**
     * Эндпоинт по возвращению всех фильмов.
     * @return Возвращает все фильмы. {@link FilmService#findAllFilms()}
     */
    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.findAllFilms();
    }

    /**
     * Эндпоинт по нахождению фильма по его id.
     * @param id идентификатор фильма.
     * @return Возвращает фильм по его id. {@link FilmService#findFilmById(Long)}
     */
    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable() Long id) {
        return filmService.findFilmById(id);
    }

    /**
     * Эндпоинт по нахождению популярных по количеству лайков фильмов.
     * @param count количество фильмов, по умолчанию 10.
     * @return Возвращает список фильмов согласна параметру count. {@link FilmService#findPopularFilms(Integer, Integer, Integer)}
     */
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                      @RequestParam (defaultValue = "-1") Integer genreId,
                                      @RequestParam (defaultValue = "-1") Integer year) {
        return  filmService.findPopularFilms(count, genreId, year);
    }

    /**
     *
     * @param query текст для поиска.
     * @param by критериий поиска, может принамать занчение director или title.
     * @return Возвращает список фильмов по пулярности отсоритрованные по критерию.
     */
    @GetMapping("/search")
    public List<Film> findByParameter(@RequestParam String query, @RequestParam String by) {
        return filmService.findByParameter(query, by);
    }

    /**
     *
     * @param directorId идентификатор режиссера.
     * @param sortBy пораметра принимает значение год или лайки.
     * @return Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.
     * {@link ru.yandex.practicum.filmorate.storage.FilmStorage#findFilmBySorting(Long, String)}
     */
    @GetMapping("/director/{directorId}")
    public List<Film> findFilmBySorting(@PathVariable Long directorId, @RequestParam String sortBy) {
        return filmService.findFilmBySorting(directorId, sortBy);

    }

    /**
     * Эндпоинт по удалению лайка фильму пользователем. {@link FilmService#deleteLike(Long, Long)}
     * @param id идентификатор фильма.
     * @param userId идентификатор пользователя.
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    /**
     * Эндпоинт по удалению фильма.
     * @param id идентификатор фильма.
     */
    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable Long id) {
        filmService.deleteFilmById(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}




