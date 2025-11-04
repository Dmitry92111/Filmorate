package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;

@Slf4j
@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public Collection<User> findAll() {
        return users.values();
    }

    public User addUser(User user) {
        log.info("Adding new user: {}", user);
        checkUniqueLoginAndEmail(user);
        user.setId(idCounter.incrementAndGet());
        users.put(user.getId(), user);
        log.info("User added successfully with id={}", user.getId());
        return user;
    }

    public User updateUser(User user) {
        log.info("Updating user: {}", user);
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.error("User update failed: ID not found ({})", user.getId());
            throw new NotFoundException(ID_NOT_FOUND + " (" + user.getId() + ")");
        }
        checkUniqueLoginAndEmail(user);
        users.put(user.getId(), user);
        log.info("User updated successfully with id={}", user.getId());
        return user;
    }

    private void checkUniqueLoginAndEmail(User user) {
        String login = user.getLogin();
        boolean isUserLoginAlreadyExist = users.values().stream()
                .anyMatch(existingUser -> Objects.equals(existingUser.getLogin(), login));
        if (isUserLoginAlreadyExist) {
            log.error("Login already exists: {}", login);
            throw new DuplicatedDataException(LOGIN_ALREADY_EXIST);
        }

        String email = user.getEmail();
        boolean isUserEmailAlreadyExist = users.values().stream()
                .anyMatch(existingUser -> Objects.equals(existingUser.getEmail(), email));
        if (isUserEmailAlreadyExist) {
            log.error("Email already exists: {}", email);
            throw new DuplicatedDataException(EMAIL_ALREADY_EXIST);
        }
    }
}
