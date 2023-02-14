package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int idGen = 1;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public InMemoryUserStorage() {
    }
    //Добавляем пользователя
    @Override
    public User createUser(User user) {
        user.setId(idGen);
        users.put(idGen, user);
        idGen++;
        log.info("Создан пользователь с ID {}: {}", user.getId(), user);
        return user;
    }
    //Обновляем пользователя
    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            log.warn("Не найден пользователь при попытке обновления.");
            throw new NotFoundException("Пользователь с ID: " + user.getId() + " не существует.");
        }
        log.info("Обновлен пользователь с ID: {}", user.getId());
        return user;
    }
    //Получаем список всех пользователей по запросу
    @Override
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            userList.add(entry.getValue());
        }
        return userList;
    }
    //Получаем конкретного пользователя
    @Override
    public User getUserById(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.warn("Не найден пользователь при поиске по ID.");
            throw new NotFoundException("Пользователь с ID: " + id + " не существует.");
        }
    }
}
