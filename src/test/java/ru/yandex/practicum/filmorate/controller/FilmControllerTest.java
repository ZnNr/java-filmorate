package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class FilmControllerTest {

    private ValidatorFactory validatorFactory;
    private Validator validator;

    @BeforeEach
    void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void close() {
        validatorFactory.close();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void checkFilmForInvalidName(String wrongName) {
        Film film = Film.builder()
                .id(1)
                .name(wrongName)
                .description("Описание фильма о том о сём о 5 о 10 1")
                .releaseDate(LocalDate.of(1980, 1, 1))
                .duration(100)
                .mpa(Mpa.builder().id(1).name("G").build())
                .build();
        Set<ConstraintViolation<Film>> violationSet = validator.validate(film);
        //Если сет с сообщениями об ошибках что-то в себе содержит, то вызовем у каждого элемента его описание
        for (ConstraintViolation<Film> violation : violationSet) {
            System.out.println("Фильм не был добавлен так как: " + violation.getMessage());
        }
        assertEquals(violationSet.size(), 1);
        assertFalse(violationSet.isEmpty()); //Если есть какие-то ошибки, то тест пройден!
    }

    @Test
    void checkFilmForInvalidDescription() {
        Film film = Film.builder()
                .id(1)
                .name("фильма о том о сём о 5 о 10")
                .description("фильма о том о сём о 5 о 10 фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10\" +\n" +
                        "                \"фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10\" +\n" +
                        "                \"фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10\" +\n" +
                        "                \"фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10фильма о том о сём о 5 о 10 !")
                .releaseDate(LocalDate.of(1980, 1, 1))
                .duration(100)
                .mpa(Mpa.builder().id(1).name("G").build())
                .build();
        Set<ConstraintViolation<Film>> violationSet = validator.validate(film);
        for (ConstraintViolation<Film> violation : violationSet) {
            System.out.println("Фильм не был добавлен так как: " + violation.getMessage());
        }
        assertEquals(violationSet.size(), 1);
        assertFalse(violationSet.isEmpty());
    }

    @Test
    void checkFilmForInvalidReleaseDate() {
        Film film = Film.builder()
                .id(1)
                .name(" ")
                .description("Описание тестового фильма 1")
                .releaseDate(LocalDate.of(1212, 12, 12))
                .duration(100)
                .mpa(Mpa.builder().id(1).name("G").build())
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(violations.size(), 1);
    }

}
