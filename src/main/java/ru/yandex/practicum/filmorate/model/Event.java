package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.utility.constants.EventType;
import ru.yandex.practicum.filmorate.utility.constants.Operation;


@Data
@AllArgsConstructor
public class Event {

    private Long timestamp;

    private Long userId;

    private EventType eventType;

    private Operation operation;

    private Long eventId;

    private Long entityId;

    public Event(Long timestamp, Long userId, EventType eventType, Operation operation, Long entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
