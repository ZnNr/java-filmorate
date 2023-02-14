package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserStorage userStorage;

    public User createUser(User user) {
        validateUser(user);
        userStorage.createUser(user);
        return user;

    }

    public User updateUser(User user) {
        validateUser(user);
        userStorage.updateUser(user);
        return user;
    }

    private void validateUser(User user) {
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


    public User addFriend(Integer id, Integer friendId) {
        User currentUser = userStorage.getUserById(id);
        User friendUser = userStorage.getUserById(friendId);
        currentUser.getFriends().add(friendId);
        friendUser.getFriends().add(id);
        log.info("Пользователи с ID {} и {} теперь друзья", id, friendId);
        return currentUser;
    }

    public User deleteFriend(Integer id, Integer friendId) {
        User currentUser = userStorage.getUserById(id);
        User friendUser = userStorage.getUserById(friendId);
        checkFriendValidate(id, friendId);
        currentUser.getFriends().remove(friendId);
        friendUser.getFriends().remove(id);
        log.info("Пользователи с ID {} и {} больше не друзья", id, friendId);
        return currentUser;
    }

    private void checkFriendValidate(Integer id, Integer friendId) {
        if (!userStorage.getUserById(id).getFriends().contains(friendId)) {
            log.error("Пользователи с id:" + id + " и id:" + friendId + " - не друзья");
            throw new ValidationException("Этого пользователя уже нет в друзьях");
        }
    }

    public List<User> getFriends(Integer id) {
        User currentUser = userStorage.getUserById(id);
        log.debug("Количество друзей пользователя c id:" + id + " = " + (int) currentUser.getFriends().stream()
                .map(userStorage::getUserById).count());
        return currentUser.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> findCommonFriends(int id, int otherId) {
        User currentUser = userStorage.getUserById(id);
        User otherUser = userStorage.getUserById(otherId);
        List<User> commonFriends = new ArrayList<>();
        List<Integer> friendsByUser = new ArrayList<>(currentUser.getFriends());
        for (int i = 0; i < currentUser.getFriends().size(); i++) {
            if (otherUser.getFriends().contains(friendsByUser.get(i))) {
                commonFriends.add(userStorage.getUserById(friendsByUser.get(i)));
            }
        }
        log.debug("Число общих друзей у пользователей с id:" + id + " и id:" + otherId + " = " + commonFriends.size());
        return commonFriends;
    }


}
