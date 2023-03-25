package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    /**
     * Метод по добавлению фильма
     *
     * @param film фильм который необходимо создать
     * @return Возвращает созданный фильм
     */
    Film addFilm(Film film);

    /**
     * Метод по модифицированию фильма
     *
     * @param film фильм, который необходимо изменить
     * @return возвращает измененный фильм
     */
    Film updateFilm(Film film);

    /**
     * Метод по возвращению всех фильмов.
     *
     * @return Возвращает все фильмы.
     */
    List<Film> findAllFilms();

    /**
     * Метод по нахождению фильма по его id.
     *
     * @param id идентификатор фильма.
     * @return Возвращает фильм по его id.
     */
    Film findFilmById(Long id);

    /**
     * Метод по нахождению популярных по количеству лайков фильмов.
     *
     * @param count количество фильмов, по умолчанию 10.
     * @return Возвращает список популярных фильмов согласна параметру count.
     */
    List<Film> getPopularFilm(Integer count);

    /**
     * @param query текст для поиска.
     * @param by    критериий поиска, может принамать занчение director или title.
     * @return Возвращает список фильмов по пулярности отсоритрованные по критерию.
     */
    List<Film> findByParameter(String query, String by);


    /**
     * @param directorId идентификатор режиссера.
     * @param sortBy     пораметра принимает значение год или лайки.
     * @return Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.
     */

    List<Film> findFilmBySorting(Long directorId, String sortBy);

    void deleteFilmById(Long id);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getPopularFilmByDateAndGenre(Integer count, Integer genreId, Integer year);
}


