package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;
    private int idGen = 1;


    public User createUser(User user) {
        validate(user);
        user.setId(idGen);
        userStorage.createUser(user);
        idGen++;
        log.info("Добавлен пользователь: " + user);
        return user;
    }

    public User updateUser(User user) {
        User beforeUser = userStorage.getUserById(user.getId());
        validate(user);
        userStorage.updateUser(user);
        log.info("Данные пользователя: " + beforeUser + " Обновлены на: " + user);
        return user;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        friendshipStorage.addAsFriend(userId, friendId);
        log.info("Пользователи с ID {} и {} теперь друзья", userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        friendshipValidate(userId, friendId);
        friendshipStorage.deleteFromFriends(userId, friendId);
        log.info("Пользователи с ID {} и {} больше не друзья", userId, friendId);
    }

    private void friendshipValidate(int userId, int friendId) {
        if (!userStorage.getUserById(userId).getFriends().contains(friendId)) {
            log.error("Пользователи с id:" + userId + " и id:" + friendId + " - не друзья");
            throw new ValidationException("Этого пользователя уже нет в друзьях");
        }
    }

    public List<User> getFriends(int userId) {
        userStorage.getUserById(userId);
        List<User> friends = friendshipStorage.getListOfFriends(userId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
        log.debug("Количество друзей пользователя c id:" + userId + " = " + friendshipStorage
                .getListOfFriends(userId).stream()
                .map(userStorage::getUserById).count());
        return friends;
    }


    public List<User> findCommonFriends(int id, int otherId) {
        userStorage.getUserById(id);
        userStorage.getUserById(otherId);
        List<User> commonFriends = friendshipStorage.getAListOfCommonFriends(id, otherId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
        log.debug("Число общих друзей у пользователей с id:" + id + " и id:" + otherId + " = " + commonFriends.size());
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
