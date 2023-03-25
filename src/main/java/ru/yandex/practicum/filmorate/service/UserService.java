package ru.yandex.practicum.filmorate.service;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.dao.RecommendationDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.constants.EventType;
import ru.yandex.practicum.filmorate.utility.constants.Operation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserStorage userStorage;

    private final FriendDao friendDao;

    private final EventService eventService;

    private final FilmStorage filmStorage;

    private final RecommendationDao recommendationDao;



    public UserService(@Qualifier("userDbStorage")
                       UserStorage userStorage,
                       FriendDao friendDao,
                       EventService eventService,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       RecommendationDao recommendationDao) {
        this.userStorage = userStorage;
        this.friendDao = friendDao;
        this.eventService = eventService;
        this.filmStorage = filmStorage;
        this.recommendationDao = recommendationDao;
    }

    /**
     * Метод по созданию пользователя. {@link UserStorage#addUser(User)}
     * @param user фильм, который необходимо создать.
     * @return Возвращает созданный объект.
     */
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.addUser(user);
    }

    /**
     * Метод по модифицированию данных пользователя.
     * @param user фильм, который необходимо изменить.
     * @return Возвращает измененного пользователя. {@link UserStorage#updateUser(User)}
     */
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    /**
     * Метод по нахождению всех пользователей.
     * @return Возвращает список всех пользователей. {@link UserStorage#getAllUsers()}
     */
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    /**
     * Метод по нахождению пользователя по id.
     * @param userId идентификатор пользователя.
     * @return Возвращает пользователя по его id. {@link UserStorage#findUserById(Long)}
     */
    public User findUserById(Long userId) {
        return userStorage.findUserById(userId);
    }

    /**
     * Метод по нахождению всех друзей определенного пользователя.
     * @param id идентификатор пользователя.
     * @return возвращает список друзей пользователя.
     */
    public List<User> findFriends(Long id) {
        userStorage.findUserById(id);
        return friendDao.findAllFriends(id);
    }

    /**
     * Метод по нахождения общих друзей.
     * @param id идентификатор пользователя.
     * @param otherId идентификатор другого пользователя.
     * @return Возвращает список общих друзей пользователя с id и otherId.
     */
    public List<User> findCommonFriends(Long id, Long otherId) {
        userStorage.findUserById(id);
        userStorage.findUserById(otherId);

        return friendDao.findCommonFriends(id, otherId);
    }

    /**
     * Метод по добавлению в друзья.
     * @param id идентификатор пользователя который добавляет в друзья.
     * @param friendId идентификатор того, кого добавляют в друзья.
     */
    public void addToFriends(Long id, Long friendId) {
        userStorage.findUserById(id);
        userStorage.findUserById(friendId);

        Event event = new Event(Instant.now().toEpochMilli(), id, EventType.FRIEND, Operation.ADD, friendId);

        friendDao.addToFriends(id, friendId);
        eventService.addEvent(event);
    }

    /**
     * Метод по удалению из друзей.
     * @param id идентификатор пользователя.
     * @param friendId идентификатор пользователя, который является другом.
     */
    public void deleteFromFriends(Long id, long friendId) {
        userStorage.findUserById(id);
        userStorage.findUserById(friendId);

        friendDao.deleteFromFriends(id, friendId);
        eventService.addEvent(new Event(Instant.now().toEpochMilli(),id, EventType.FRIEND, Operation.REMOVE, friendId));
    }

    public void deleteUserById(Long id) {
        userStorage.deleteUserById(id);
        eventService.deleteEvent(id);
    }

    /**
     * Получения списка рекомендованных фильмов
     * @param id - айди юзера
     * @return - список фильмов
     */
    public List<Film> getRecommendationFilms(long id) {
        userStorage.findUserById(id);
        List<Film> resultList = new ArrayList<>();
        Optional<Long> idCommonFilmsWithCurrentId = recommendationDao.getIdCommonFilmWithCurrentId(id);
        if (idCommonFilmsWithCurrentId.isPresent()) {
            List<Long> listFilmsNotInId =
                    recommendationDao.getLikedFilmWithoutLiked(Optional.of(id), idCommonFilmsWithCurrentId);
            if (!listFilmsNotInId.isEmpty()) {
                resultList = recommendationDao.getListFilm(listFilmsNotInId);
            }
        }
        return resultList;
    }


    public List<User> getMutualFriendsById(Long firstUserId, Long secondUserId) {
        if (!(userStorage.containsUser(firstUserId) && userStorage.containsUser(secondUserId))) {
            throw new ElementNotFoundException("Пользователь с id:" + firstUserId + "не может быть найден");
        }
        Set<Long> userFriendsIds = userStorage.getSubscribers(firstUserId).stream().map(User::getId).collect(Collectors.toSet());
        Set<Long> otherUserFriendsIds = userStorage.getSubscribers(secondUserId).stream().map(User::getId).collect(Collectors.toSet());
        userFriendsIds.retainAll(otherUserFriendsIds);
        List<User> commonFriends = new ArrayList<>();
        userFriendsIds.forEach(friendId -> userStorage.get(friendId).ifPresent(commonFriends::add));
        return commonFriends;
    }

    public void removeSubscribe(Long authorId, Long subscriberId) {
        if (!userStorage.containsUser(authorId) || !userStorage.containsUser(subscriberId)) {
            throw new ElementNotFoundException(String.format("Юзера с id %d не существует", subscriberId));
        }
        if (!isSubscribe(authorId, subscriberId)) {
            throw new ElementNotFoundException("Вы не подписаны на этого автора");
        }
        userStorage.deleteSubscriber(authorId, subscriberId);
    }

    public boolean isSubscribe(Long authorId, Long subscriberId) {
        return userStorage.checkIsSubscriber(authorId, subscriberId);
    }


    public void makeSubscribe(Long authorId, Long subscriberId) {
        if (!userStorage.containsUser(authorId) || !userStorage.containsUser(subscriberId)) {
            throw new ElementNotFoundException("Пользователя с id" + subscriberId + " не существует.");
        }
        if (isSubscribe(authorId, subscriberId)) {
            throw new ElementNotFoundException("");
        }
        userStorage.createSubscriber(authorId, subscriberId);
    }


}
