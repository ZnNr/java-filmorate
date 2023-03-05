package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserControllerTest {
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

    @Test
    void addNewUserTest() {
        User user = User.builder()
                .id(1)
                .email("user@yandex.ru")
                .login("user1")
                .name("polzovatel")
                .birthday(LocalDate.of(1997, 6, 28))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void checkUserForInvalidEmail() {
        User user = User.builder()
                .id(1)
                .email("sgsdgsdgsdg")
                .login("user1")
                .name("polzovatel")
                .birthday(LocalDate.of(1997, 6, 28))
                .build();
        Set<ConstraintViolation<User>> violationSet = validator.validate(user);
        for (ConstraintViolation<User> violation : violationSet) {
            System.out.println("Пользователь не был добавлен так как: " + violation.getMessage());
        }
        assertFalse(violationSet.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void checkUserForInvalidEmptyLogin(String wrongLogin) {
        User user = User.builder()
                .id(1)
                .email("user@yandex.ru")
                .login(wrongLogin)
                .name("polzovatel")
                .birthday(LocalDate.of(1997, 6, 28))
                .build();
        Set<ConstraintViolation<User>> violationSet = validator.validate(user);
        for (ConstraintViolation<User> violation : violationSet) {
            System.out.println("Пользователь не был добавлен так как: " + violation.getMessage());
        }
        assertFalse(violationSet.isEmpty());
    }

    @Test
    void checkUserForInvalidBirthday() {
        User user = User.builder()
                .id(1)
                .email("user@yandex.ru")
                .login("user1")
                .name("polzovatel")
                .birthday(LocalDate.of(3000, 6, 28))
                .build();
        Set<ConstraintViolation<User>> violationSet = validator.validate(user);
        for (ConstraintViolation<User> violation : violationSet) {
            System.out.println("Пользователь не был добавлен так как: " + violation.getMessage());
        }
        assertFalse(violationSet.isEmpty());
    }
}
