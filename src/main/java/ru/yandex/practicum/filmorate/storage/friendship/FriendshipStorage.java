package ru.yandex.practicum.filmorate.storage.friendship;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Optional;
import java.util.Set;

public interface FriendshipStorage {
    void add(Friendship friendship);

    void update(Friendship friendship);

    boolean remove(Long userId, Long friendId);

    boolean exists(Long userId, Long friendId);

    Set<Friendship> findAllFriends(Long userId);

    Optional<Friendship> find(Long userId, Long friendId);
}
