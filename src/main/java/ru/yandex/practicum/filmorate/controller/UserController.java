package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addNewUser(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("Добавление пользователя: Создан пользователь с ID {}: {}", user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validateUser(user);
        if (users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
            log.info("Обновлен пользователь с ID {}", user.getId());
            return user;
        } else {
            log.warn("Пользователь с таким id не существует или не найден");
            throw new UserNotFoundException("Пользователь с ID " + user.getId() + " не существует.");
        }
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        List<User> userList = new ArrayList<>();
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            userList.add(entry.getValue());
        }
        return userList;
    }


    private void validateUser(User user) {

        if (user == null) {
            log.warn("Ошибка: Пользователь не может быть null.");
            throw new ValidationException("Ошибка: Пользователь не может быть null.");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка: Некорректный логин");
            throw new ValidationException("Ошибка: Некорректный логин");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка: Логин не должен быть пустым и не должен содержать пробелы.");
            throw new ValidationException("Ошибка: Логин не должен быть пустым и не должен содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Запись в поле имени значение логина, если полученное имя пустое");
            user.setName(user.getLogin());

        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка: Дата рождения не может быть в будущем");
            throw new ValidationException("Ошибка: Дата рождения не может быть в будущем");
        }
        if (!user.getEmail().contains("@") || user.getEmail().isBlank() || user.getEmail().contains(" ")) {
            log.warn("Ошибка: email пуст или не содержит знака @.");
            throw new ValidationException("Ошибка: email не содержит знака @.");
        }
    }
}
