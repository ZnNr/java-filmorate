package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTests {

    private FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    public void shouldNotValidateIfNameIsNull() {
        Film nullFilm = new Film(0, "", "description", LocalDate.of(2019, 8, 12), 120);
        assertThrows(ValidationException.class, () -> {
            filmController.addFilm(nullFilm);
        });
        Film noFilm = new Film(1, "", "description", LocalDate.of(2019, 8, 12), 120);
        assertThrows(ValidationException.class, () -> {
            filmController.addFilm(noFilm);
        });
        Film gapFilm = new Film(2, "", "description", LocalDate.of(2019, 8, 12), 120);
        assertThrows(ValidationException.class, () -> {
            filmController.addFilm(gapFilm);
        });
    }


    @Test
    public void shouldNotValidateIfLongDescription() {
        Film film = new Film(1, "Вавилон",
                "В 1920-х годах немое кино продолжает пользоваться популярностью, но постепенно на первый план выходят новые картины со звуком, "
                        + "в то время как Голливуд стремительно превращается в райский уголок для деятелей киноиндустрии. Роскошная жизнь, богатство и "
                        + "возможность обрести мировую известность привлекают одаренных, но начинающих актеров с разных уголков страны, стремящихся отобрать "
                        + "популярность у «старой гвардии» Голливуда, которым предстоит бороться за личное будущее в динамично развивающейся киноиндустрии. "
                        + "Пока звезда немого кино Джек Конрад пытается найти себя в мире звуковых фильмов, дерзкая Нелли из Нью-Джерси, помощник продюсера "
                        + "Мэнни и джазовый музыкант Сидни пробивают собственный путь к славе…", LocalDate.of(1922, 7, 21), 120);
        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertEquals("Описание превышает 200 символов.", thrown.getMessage());
    }

    @Test
    public void shouldNotValidateOldReleaseDate() {
        Film film = new Film(1, "name", "description",
                LocalDate.of(1895, 3, 22), 1);
        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertEquals("Дата релиза фильма должна быть после 1895-12-28", thrown.getMessage());

        Film film2 = new Film(0, "Пограничный фильм", "description",
                LocalDate.of(1895, 12, 28), 1);
        assertDoesNotThrow(() -> {
            filmController.addFilm(film2);
        });
    }

    @Test
    public void shouldNotValidateIfDurationIsNegative() {
        Film film = new Film(0, "Minus 1", "Movie back in time",
                LocalDate.of(2000, 1, 1), -7);
        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            filmController.addFilm(film);
        });
        assertEquals("Продолжительность фильма не может быть отрицательным.", thrown.getMessage());

        Film film2 = new Film(1, "Фильм с нулевой длительностью", "но не в секундах",
                LocalDate.of(2000, 1, 1), 0);
        assertDoesNotThrow(() -> {
            filmController.addFilm(film2);
        });
    }

}
