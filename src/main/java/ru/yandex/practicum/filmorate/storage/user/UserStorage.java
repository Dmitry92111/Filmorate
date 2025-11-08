package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Optional<User> findById(Long id);

    User save(User user);

    User update(User user);

    Collection<User> findAll();

    void deleteById(Long id);
}
