package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.utility.constants.EventType;
import ru.yandex.practicum.filmorate.utility.constants.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Component
@RequiredArgsConstructor
public class EventDaoImpl implements EventDao {

    private static final String SQL_ADD_EVENT = "INSERT INTO event(time_stamp, user_id, event_type, operation, entity_id) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_FIND_EVENTS_BY_USER = "SELECT * FROM event WHERE user_id = ?";

    private static final String SQL_REMOVE_EVENT_BY_USER = "DELETE FROM event WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEvent(Event event) {
       jdbcTemplate.update(SQL_ADD_EVENT,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId());
    }

    @Override
    public List<Event> findAllEventsByUserId(Long userId) {
        List<Event> events = jdbcTemplate.query(SQL_FIND_EVENTS_BY_USER, this::makeEvent, userId);
        if (events.isEmpty()) {
            throw new ElementNotFoundException(String.format("Событий принадлежащие пользователю с ID=%d нет.", userId));
        }

        return events;
    }

    @Override
    public void deleteEvent(Long id) {
        jdbcTemplate.update(SQL_REMOVE_EVENT_BY_USER, id);
    }

    private Event makeEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return new Event(
                resultSet.getLong("time_stamp"),
                resultSet.getLong("user_id"),
                EventType.valueOf(resultSet.getString("event_type")),
                Operation.valueOf(resultSet.getString("operation")),
                resultSet.getLong("event_id"),
                resultSet.getLong("entity_id"));
    }
}