package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    /**
     * Метод по созданию пользователя.
     * @param user фильм, который необходимо создать.
     * @return Возвращает созданный объект.
     */
    User addUser(User user);

    /**
     * Метод по модифицированию данных пользователя.
     * @param user фильм, который необходимо изменить.
     * @return Возвращает измененного пользователя.
     */
    User updateUser(User user);

    /**
     * Метод по нахождению пользователя по id.
     *
     * @param userId идентификатор пользователя.
     * @return Возвращает пользователя по его id.
     */
    User findUserById(Long userId);

    /**
     * Метод по нахождению всех пользователей.
     * @return Возвращает список всех пользователей.
     */
    List<User> getAllUsers();

    void deleteUserById(Long id);

     boolean containsUser(Long id);

    Optional<User> get(Long id);

    void createSubscriber(Long authorId, Long subscriberId);

    boolean checkIsSubscriber(Long authorId, Long subscriberId);

    void deleteSubscriber(Long authorId, Long subscriberId);

    List<User> getSubscribers(Long id);
}
