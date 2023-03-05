package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorageInMemory {

    List<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(int id);
}
