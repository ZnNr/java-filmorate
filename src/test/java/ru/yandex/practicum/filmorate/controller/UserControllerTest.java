package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private static UserController userController;
    private User user;

    @BeforeAll
    public static void createController() {
        userController = new UserController();
    }

    @BeforeEach
    public void addNewUser() {
        user = new User(0, "zinnurshamsutdinov@yandex.ru", "login", "name",
                LocalDate.of(1985, Month.FEBRUARY, 15));
    }

    @Test
    void shouldExceptionWithNull() {
        ValidationException e = assertThrows(ValidationException.class, () -> userController.addNewUser(null));
        assertEquals("Ошибка: Пользователь не может быть null.", e.getMessage());
    }

    @Test
    void shouldExceptionWithEmptyLogin() {
        user.setLogin("");
        ValidationException e = assertThrows(ValidationException.class, () -> userController.addNewUser(user));
        assertEquals("Ошибка: Логин не должен быть пустым и не должен содержать пробелы.", e.getMessage());
    }

    @Test
    void shouldExceptionWithIncorrectLogin() {
        user.setLogin("log in");
        ValidationException e = assertThrows(ValidationException.class, () -> userController.addNewUser(user));
        assertEquals("Ошибка: Некорректный логин", e.getMessage());
    }

    @Test
    void shouldExceptionWithIncorrectEmail() {
        user.setEmail("ddd.dddfdddd");
        ValidationException e = assertThrows(ValidationException.class, () -> userController.addNewUser(user));
        assertEquals("Ошибка: email не содержит знака @.", e.getMessage());
    }


    @Test
    void shouldExceptionUpdateWithNonContainsId() {
        user.setId(10);
        UserNotFoundException s = assertThrows(UserNotFoundException.class, () -> userController.updateUser(user));
        assertEquals("Пользователь с ID 10 не существует.", s.getMessage());
    }

    @Test
    public void shouldReplaceNameWithLogin() {
        User noNameUser = new User(26, "test@test.ru", "nox", "", LocalDate.of(1998, 8, 29));
        User addNewUser = userController.addNewUser(noNameUser);
        assertEquals(addNewUser.getName(), "nox");
    }


    @Test
    public void shouldNotValidateIfBirthdayInFuture() {
        User user = new User(25, "test@test.ru", "nox", "zZinnur", LocalDate.of(2023, 3, 29));
        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            userController.addNewUser(user);
        });
        assertEquals("Ошибка: Дата рождения не может быть в будущем", thrown.getMessage());
    }

}