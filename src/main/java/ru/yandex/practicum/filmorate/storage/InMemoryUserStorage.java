package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Subscriber;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private long generatedId = 1;

    private final Map<Long, User> users = new HashMap<>();


    private long generateId() {
        return generatedId++;
    }

    @Override
    public User addUser(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new UserAlreadyExistException(String.format("Пользователь с почтой %s уже существует", user.getEmail()));
        }

        user.setId(generatedId++);
        users.put(user.getId(), user);
        return user;
    }


    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ElementNotFoundException(
                    String.format("Не возможно обновить данные пользователя с несуществующем id - %d", user.getId()));
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new ElementNotFoundException(
                    String.format("Пользователь с id - %d не найден", userId));
        }
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUserById(Long id) {
        users.remove(id);
    }

    @Override
    public boolean containsUser(Long id) {
        return get(id).isPresent();
    }

    @Override
    public Optional<User> get(Long id) {
        log.info("Поиск юзера по id={}", id);
        String sql = "SELECT U.*, group_concat(S.SUBSCRIBER separator ',') AS SUBSCRIBERS\n" +
                "FROM USERS U\n" +
                "LEFT JOIN SUBSCRIBES S ON U.ID = S.AUTHOR WHERE U.ID = ?\n" +
                "GROUP BY U.ID";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::userBuilder, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<User> getSubscribers(Long id) {
        log.info("Поиск всех подписчиков");
        String sql = "SELECT U.*\n" +
                "FROM SUBSCRIBES\n" +
                "JOIN USERS U on U.ID = SUBSCRIBES.SUBSCRIBER\n" +
                "WHERE AUTHOR=?";
        return jdbcTemplate.query(sql, this::userBuilder, id);
    }

    private User userBuilder(ResultSet rs, int rowNum) throws SQLException {
        String subscribers;
        try {
            subscribers = rs.getString("SUBSCRIBERS");
        } catch (SQLException e) {
            subscribers = null;
        }
        List<Long> subscribersList = subscribers != null ? Arrays
                .stream(subscribers.split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList()) : null;
        return User.builder()
                .id(rs.getLong("ID"))
                .email(rs.getString("EMAIL"))
                .login(rs.getString("LOGIN"))
                .name(rs.getString("NAME"))
                .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                .subscribers(subscribersList)
                .build();
    }

    private Subscriber subscribeBuilder(ResultSet rs, int rowNum) throws SQLException {
        return Subscriber.builder()
                .authorId(rs.getInt("AUTHOR"))
                .subscriberId(rs.getInt("SUBSCRIBER"))
                .build();
    }

    @Override
    public void createSubscriber(Long authorId, Long subscriberId) {
        String sql = "INSERT INTO SUBSCRIBES (AUTHOR, SUBSCRIBER) VALUES (?, ?)";
        jdbcTemplate.update(sql, authorId, subscriberId);
        log.info("Подписка {} на {} создана", subscriberId, authorId);
    }

    @Override
    public boolean checkIsSubscriber(Long authorId, Long subscriberId) {
        String sql = "SELECT * FROM SUBSCRIBES WHERE AUTHOR=? AND SUBSCRIBER=?";
        return !jdbcTemplate.query(sql, this::subscribeBuilder, authorId, subscriberId).isEmpty();
    }

    @Override
    public void deleteSubscriber(Long authorId, Long subscriberId) {
        String sql = "DELETE FROM SUBSCRIBES WHERE AUTHOR=? AND SUBSCRIBER=?";
        jdbcTemplate.update(sql, authorId, subscriberId);
        log.info("Подписка {} на {} удалена", subscriberId, authorId);
    }

}
