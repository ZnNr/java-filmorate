package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmValidationTests {
    private static FilmStorage filmStorage;

    private static FilmService filmService;
    private static FilmController filmController;
    private static UserStorage userStorage;
    private static UserService userService;
    private Film film;

    @BeforeAll
    public static void createController() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmStorage, filmService);
    }

    @BeforeEach
    public void createFilm() {
        film = new Film(0, "name", "description", LocalDate.of(2005, 05, 15), 100);
    }


    @Test
    void shouldExceptionWithEmptyName() {
        film.setName("");
        ValidationException e = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Название не может быть пустым.", e.getMessage());
    }

    @Test
    void shouldExceptionWithTooLongDescription() {
        film.setDescription("a".repeat(201));
        ValidationException e = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Описание превышает 200 символов.", e.getMessage());
    }

    @Test
    void shouldExceptionWithNegativeDuration() {
        film.setDuration(-1);
        ValidationException e = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Продолжительность фильма не может быть отрицательным.", e.getMessage());
    }

    @Test
    void shouldExceptionWithIncorrectReleaseDay() {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 15));
        ValidationException e = assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        assertEquals("Дата релиза фильма должна быть после 1895-12-28", e.getMessage());
    }

    @Test
    void shouldExceptionUpdateWithNonContainsId() {
        film.setId(10);
        NotFoundException e = assertThrows(NotFoundException.class, () -> filmController.updateFilm(film));
        assertEquals("Такого фильма не существует!", e.getMessage());
    }
}


