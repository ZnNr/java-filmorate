package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorageInMemory {
    User getUserById(Integer id);
    List<User> getAllUsers();

    User addUser(User user);

    User updateNewUser(User user);


}
