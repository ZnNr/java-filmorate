package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
public interface FriendshipStorage {


    boolean addAsFriend(int userId, int friendId);

    boolean deleteFromFriends(int userId, int friendId);

    HashSet<Integer> getListOfFriends(int userId);

    List<Integer> getAListOfCommonFriends(int userId, int otherId);

}
