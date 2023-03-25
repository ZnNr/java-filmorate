package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventDao eventDao;

    private final UserDbStorage userDbStorage;

    public void addEvent(Event event) {
        eventDao.addEvent(event);
    }

    public List<Event> findAllEventsByUserId(Long userId) {
        userDbStorage.findUserById(userId);
        return eventDao.findAllEventsByUserId(userId);
    }

    public void deleteEvent(Long id) {
        eventDao.deleteEvent(id);
    }

}
