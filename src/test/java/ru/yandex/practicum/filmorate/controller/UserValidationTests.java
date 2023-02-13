package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidationTests {

    private UserController userController;

    @BeforeEach
    public void beforeEach() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
    }

    @Test
    public void shouldNotValidateIfNullEmail() {
        User nullEmailUser = new User(null, "zinnurshamsutdinov", "Zinnur", LocalDate.of(1998, 8, 29));
        assertThrows(ValidationException.class, () -> {
            userController.createUser(nullEmailUser);
        });
        User noEmailUser = new User("", "zinnurshamsutdinov", "Zinnur", LocalDate.of(1998, 8, 29));
        assertThrows(ValidationException.class, () -> {
            userController.createUser(noEmailUser);
        });
        User gapEmailUser = new User(" ", "zinnurshamsutdinov", "Zinnur", LocalDate.of(1998, 8, 29));
        assertThrows(ValidationException.class, () -> {
            userController.createUser(gapEmailUser);
        });
    }

    @Test
    public void shouldNotValidateIfNoAt() {
        User wrongEmailUser = new User("google.com", "zinnurshamsutdinov", "Zinnur", LocalDate.of(1998, 8, 29));
        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            userController.createUser(wrongEmailUser);
        });
        assertEquals("Адрес электронной почты не может быть пустым и должен содержать символ @.", thrown.getMessage());
    }

    @Test
    public void shouldNotValidateIfLoginNull() {
        User nullLoginUser = new User("test@test.ru", null, "Zinnur", LocalDate.of(1998, 8, 29));
        assertThrows(ValidationException.class, () -> {
            userController.createUser(nullLoginUser);
        });
        User noLoginUser = new User("test@test.ru", "", "Zinnur", LocalDate.of(1998, 8, 29));
        assertThrows(ValidationException.class, () -> {
            userController.createUser(noLoginUser);
        });
        User gapLoginUser = new User("test@test.ru", " ", "Zinnur", LocalDate.of(1998, 8, 29));
        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            userController.createUser(gapLoginUser);
        });
        assertEquals("Логин не должен быть пустым и не должен содержать пробелы.", thrown.getMessage());
    }

    @Test
    public void shouldReplaceNameWithLogin() {
        User noNameUser = new User("test@test.ru", "zinnurshamsutdinov", "", LocalDate.of(1998, 8, 29));
        User createdUser = userController.createUser(noNameUser);
        assertEquals(createdUser.getName(), "zinnurshamsutdinov");
    }

    @Test
    public void shouldNotValidateIfBirthdayInFuture() {
        User user = new User("test@test.ru", "zinnurshamsutdinov", "Zinnur", LocalDate.of(2023, 3, 29));
        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
        assertEquals("Дата рождения не может быть в будущем.", thrown.getMessage());
    }
}
