package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Subscriber;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.utility.mapper.UserMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDbStorage implements UserStorage {


    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_INSERT_INTO_USERS = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (:email, :login, :name, :birthday)";

    private static final String SQL_UPDATE_USER = "UPDATE users SET email = :email," +
            " login = :login, name = :name, birthday = :birthday WHERE user_id = :user_id";

    private static final String SQL_FIND_USER_BY_ID = "SELECT * FROM users WHERE user_id = :user_id";

    private static final String SQL_GET_ALL_USERS = "SELECT * FROM USERS";

    private static final String SQL_DELETE_USER_BY_ID = "DELETE FROM users WHERE user_id = :user_id";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public User addUser(User user) {
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());
        try {
            namedParameterJdbcTemplate.update(SQL_INSERT_INTO_USERS, parameters, holder);
        } catch (DuplicateKeyException e) {
            if (e.getMessage().contains("EMAIL"))
                throw new UserAlreadyExistException(String.format(
                        "Пользователь с почтой=%S уже существует!", user.getEmail()));
            else
                throw new UserAlreadyExistException(String.format(
                        "Пользователь с логином=%S уже существует!", user.getLogin()));
        }

        user.setId(holder.getKey().longValue());
        log.debug("Добавлен пользователь [{}]", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", user.getId())
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday());

        int check = namedParameterJdbcTemplate.update(SQL_UPDATE_USER, parameters);

        if (check == 0) {
            throw new ElementNotFoundException(String.format("Пользователя с id=%d не существует!", user.getId()));
        }

        log.debug("Пользователь с id={} обнавлен.", user.getId());
        return findUserById(user.getId());
    }

    @Override
    public User findUserById(Long userId) {
        log.debug("Запрошен пользователь с id={}", userId);
        return namedParameterJdbcTemplate.query(
                        SQL_FIND_USER_BY_ID, new MapSqlParameterSource("user_id", userId), new UserMapper())
                .stream()
                .findFirst()
                .orElseThrow(() -> new ElementNotFoundException(
                        String.format("Пользователя с id=%d не существует!", userId)));
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Запрошен список всех пользователей.");
        return namedParameterJdbcTemplate.query(SQL_GET_ALL_USERS, new UserMapper());
    }

    @Override
    public void deleteUserById(Long id) {
        namedParameterJdbcTemplate.update(SQL_DELETE_USER_BY_ID, new MapSqlParameterSource("user_id", id));
    }

    @Override
    public boolean containsUser(Long id) {
        return false;
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.empty();
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

    @Override
    public List<User> getSubscribers(Long id) {
        log.info("Поиск всех подписчиков");
        String sql = "SELECT U.*\n" +
                "FROM SUBSCRIBES\n" +
                "JOIN USERS U on U.ID = SUBSCRIBES.SUBSCRIBER\n" +
                "WHERE AUTHOR=?";
        return jdbcTemplate.query(sql, this::userBuilder, id);
    }

    private Subscriber subscribeBuilder(ResultSet rs, int rowNum) throws SQLException {
        return Subscriber
                .builder()
                .authorId(rs.getInt("AUTHOR"))
                .subscriberId(rs.getInt("SUBSCRIBER"))
                .build();
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
}
