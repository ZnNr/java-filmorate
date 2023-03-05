package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;


    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
         validate(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validate(user);
        return userStorage.updateUser(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        log.info("Пользователи с ID {} и {} теперь друзья", id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        userStorage.getUserById(id).getFriends().remove(friendId);
        userStorage.getUserById(friendId).getFriends().remove(id);
        log.info("Пользователи с ID {} и {} больше не друзья", id, friendId);
    }

    public List<User> getFriends(int id) {
        List<User> friends = new ArrayList<>();
        User user = userStorage.getUserById(id);
        if (user.getFriends() != null) {
            for (int userId : user.getFriends()) {
                friends.add(userStorage.getUserById(userId));
            }
        }
        return friends;
    }

    public List<User> findCommonFriends(int id, int friendId) {
        List<User> commonFriends = new ArrayList<>();
        User user = userStorage.getUserById(id);
        User friends = userStorage.getUserById(friendId);
        for (Integer friend : user.getFriends()) {
            if (friends.getFriends().contains(friend)) {
                commonFriends.add(userStorage.getUserById(friend));
            }
        }
        return commonFriends;
    }

    private void validate(User user) {
        if ((user.getEmail() == null || user.getEmail().isBlank()) || !user.getEmail().contains("@")) {
            log.warn("Валидация не пройдена: email либо пустой, либо не содержит @.");
            throw new ValidationException("Адрес электронной почты не может быть пустым и должен содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Валидация не пройдена: логин либо пустой, либо содержит пробелы.");
            throw new ValidationException("Логин не должен быть пустым и не должен содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация не пройдена: дата рождения в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }


}
