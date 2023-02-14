package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTests {

    private static UserStorage userStorage;
    private static UserService userService;
    private static UserController userController;
    private User user;

    @BeforeAll
    public static void createController() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @BeforeEach
    public void createUser() {
        user = new User(0, "test@test.ru", "login", "name",
                LocalDate.of(2000, 02, 15));
    }

    @Test
    void shouldExceptionWithEmptyLogin() {
        user.setLogin("");
        ValidationException e = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Логин не должен быть пустым и не должен содержать пробелы.", e.getMessage());
    }

    @Test
    void shouldExceptionWithIncorrectLogin() {
        user.setLogin("log in");
        ValidationException e = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Логин не должен быть пустым и не должен содержать пробелы.", e.getMessage());
    }

    @Test
    void shouldExceptionWithIncorrectEmail() {
        user.setEmail("ddd.dddfdddd");
        ValidationException e = assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals("Адрес электронной почты не может быть пустым и должен содержать символ @.", e.getMessage());
    }

    @Test
    void shouldExceptionUpdateWithNonContainsId() {
        user.setId(10);
        NotFoundException e = assertThrows(NotFoundException.class, () -> userController.updateUser(user));
        assertEquals("Пользователь с ID: 10 не существует.", e.getMessage());
    }

    @Test
    void shouldNotExceptionWithEmptyName() {
        user.setName("");
        userController.createUser(user);
        assertEquals(user.getName(), user.getLogin());
    }
}
