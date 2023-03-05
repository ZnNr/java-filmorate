package ru.yandex.practicum.filmorate.storage.inMemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorageInMemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Component
public class InMemoryUserStorage implements UserStorageInMemory {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final HashMap<Integer, User> users = new HashMap<>();
    private int idGen = 1;

    @Autowired
    public InMemoryUserStorage() {
    }

    //Добавляем пользователя
    @Override
    public User addUser(User user) {
        user.setId(idGen);
        users.put(idGen, user);
        idGen++;
        log.info("Создан пользователь с ID {}: {}", user.getId(), user);
        return user;
    }

    //Обновляем пользователя
    @Override
    public User updateNewUser(User user) {
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
    public List<User> getAllUsers() {
        log.debug("Количество пользователей всего: {}", users.size());
        return new ArrayList<>(users.values());
    }

    //Получаем конкретного пользователя
    @Override
    public User getUserById(Integer id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.warn("Не найден пользователь при поиске по ID.");
            throw new NotFoundException("Пользователь с ID: " + id + " не существует.");
        }
    }
}
