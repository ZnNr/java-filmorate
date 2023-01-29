package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




@Slf4j
@RestController
@RequestMapping("/usersMap")
public class UserController {

    private int id = 1;
    private final Map<Integer, User> usersMap = new HashMap<>();

    @PostMapping
    public User addNewUser(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(id++);
        usersMap.put(user.getId(), user);
        log.info("Добавление пользователя: Создан пользователь с ID {}: {}", user.getId(), user);
        return user;
    }

    public String isMatchFound(@NotBlank String email) {
        //Set the email pattern string
        Pattern p = Pattern.compile(" (?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"
                + "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")"
                + "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)"
                + "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:"
                + "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
        //Match the user email  with the pattern


        Matcher m = p.matcher(email);
        boolean matches = m.matches();
        String findMaches = null;
        if (matches) {
            findMaches = email;
        }
        return  findMaches;
    }
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validateUser(user);
        if (usersMap.containsKey(user.getId())) {
            usersMap.replace(user.getId(), user);
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
        for (Map.Entry<Integer, User> entry : usersMap.entrySet()) {
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
        if (!user.getEmail().contains("@")) {
            log.warn("Ошибка: email пуст или не содержит знака @.");
            throw new ValidationException("Ошибка: email не содержит знака @.");
        }

        @NotBlank String email = null;
        if (user.getEmail() == isMatchFound(user.getEmail())) {
            log.warn("Ошибка: email некорректен или содержит недопустимые символы.");
            throw new ValidationException("Ошибка: email некорректен или содержит недопустимые символы.");
        }


    }




}
