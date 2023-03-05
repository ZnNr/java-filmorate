package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.interfaces.FriendshipStorage;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addAsFriend(int userId, int friendId) {
        Friendship friends = Friendship.builder()
                .userId(userId)
                .friendId(friendId)
                .build();
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friendship");
        return simpleJdbcInsert.execute(toMap(friends)) > 0;
    }

    @Override
    public boolean deleteFromFriends(int userId, int friendId) {
        String sqlQuery = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sqlQuery, userId, friendId) > 0;
    }

    @Override
    public HashSet<Integer> getListOfFriends(int userId) {
        String sqlQuery = "SELECT friend_id FROM friendship WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sqlQuery, Integer.class, userId));
    }

    @Override
    public List<Integer> getAListOfCommonFriends(int userId, int otherId) {
        String sqlQuery = "SELECT friend_id " +
                "from (SELECT *  FROM friendship where user_id = ? OR user_id = ?) " +
                "GROUP BY friend_id HAVING (COUNT(*) > 1)";
        return jdbcTemplate.queryForList(sqlQuery, Integer.class, userId, otherId);
    }


    private Map<String, Object> toMap(Friendship friends) {
        Map<String, Object> values = new HashMap<>();
        values.put("user_Id", friends.getUserId());
        values.put("friend_Id", friends.getFriendId());
        return values;
    }
}
