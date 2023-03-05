package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User getUserById(int userId);

    User createUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();


}