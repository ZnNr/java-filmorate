package ru.yandex.practicum.filmorate.storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> userMap = new HashMap<>();

    @Override
    public User createUser(User user) {
        userMap.put(user.getId(), user);
        log.info("Создан пользователь с ID {}: {}", user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (userMap.containsKey(user.getId())) {
            userMap.put(user.getId(), user);
        } else {
            log.warn("Не найден пользователь при попытке обновления.");
            throw new UserNotFoundException("Пользователь с ID: " + user.getId() + " не существует.");
        }
        log.info("Обновлен пользователь с ID: {}", user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();
        for (Map.Entry<Integer, User> entry : userMap.entrySet()) {
            userList.add(entry.getValue());
        }
        return userList;
    }

    @Override
    public User getUserById(int id) {
        if (userMap.containsKey(id)) {
            return userMap.get(id);
        } else {
            log.warn("Не найден пользователь при поиске по ID.");
            throw new UserNotFoundException("Пользователь с ID: " + id + " не существует.");
        }
    }
}
