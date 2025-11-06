package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;
import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.EMAIL_ALREADY_EXIST;

@Slf4j
@Service
public class UserService {
    UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        log.info("Adding new user: {}", user);
        checkUniqueLoginAndEmail(user);
        userStorage.save(user);
        log.info("User added successfully with id={}", user.getId());
        return user;
    }

    public User findById(Long id) {
        log.info("Trying to find user by ID: {}", id);
        if (id == null) {
            log.error("User search failed, method got null instead of ID number");
            throw new ConditionsNotMetException(ID_CANNOT_BE_NULL);
        }

        return userStorage.findById(id).
                map(user -> {
                    log.info("User has been found successfully with id: {}", id);
                    return user;
                })
                .orElseThrow(() -> {
                    log.error("User search failed: ID not found ({})", id);
                    return new NotFoundException(USER_BY_ID_NOT_FOUND + " (" + id + ")");
                });
    }

    public User updateUser(User user) {
        log.info("Updating user: {}", user);
        User existingUser = findById(user.getId());
        if (!user.getEmail().equals(existingUser.getEmail())) {
            checkUniqueEmail(user.getEmail());
            existingUser.setEmail(user.getEmail());
        }
        if (!user.getLogin().equals(existingUser.getLogin())) {
            checkUniqueLogin(user.getLogin());
            existingUser.setLogin(user.getLogin());
        }
        existingUser.setBirthday(user.getBirthday());
        existingUser.setName(user.getName());

        userStorage.update(existingUser);
        log.info("User updated successfully with id={}", user.getId());
        return existingUser;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }


    public void addFriend(Long userId, Long friendId) {
        log.info("Trying to make users friends: {} & {}", userId, friendId);
        User user = findById(userId);
        User friend = findById(friendId);

        user.getFriendsIds().add(friendId);
        friend.getFriendsIds().add(userId);
        log.info("users {} and {} have successfully become friends", userId, friendId);
    }

    public void removeFromFriends(Long userId, Long friendId) {
        log.info("Trying to remove user {} from friend-list of user {}", friendId, userId);
        User user = findById(userId);
        if (!user.getFriendsIds().contains(friendId)) {
            log.error("User with id {} don't have a friend with id {}", userId, friendId);
            throw new ConditionsNotMetException(USER_NOT_FOUND_IN_FRIEND_LIST);
        }
        User friend = findById(friendId);
        user.getFriendsIds().remove(friendId);
        friend.getFriendsIds().remove(userId);
        log.info("users {} and {} have successfully stopped being friends", userId, friendId);
    }

    public Collection<User> getListOfFriends(Long userId) {
        User user = findById(userId);
        return user.getFriendsIds().stream().map(this::findById).toList();
    }

    public Collection<User> getListOfCommonFriends(Long userId, Long anotherUserId) {
        log.info("Trying to get list of common friends of users {} and {}", userId, anotherUserId);
        User user = findById(userId);
        User anotherUser = findById(anotherUserId);

        List<User> commonFriends = user.getFriendsIds().stream()
                .filter(anotherUser.getFriendsIds()::contains).map(this::findById).toList();
        log.info("Common friends of users {} and {} have been found", userId, anotherUserId);
        return commonFriends;
    }

    private void checkUniqueLoginAndEmail(User user) {
        checkUniqueLogin(user.getLogin());
        checkUniqueEmail(user.getEmail());
    }

    private void checkUniqueLogin(String login) {
        boolean isUserLoginAlreadyExist = userStorage.findAll().stream()
                .anyMatch(existingUser -> Objects.equals(existingUser.getLogin(), login));
        if (isUserLoginAlreadyExist) {
            log.error("Login already exists: {}", login);
            throw new DuplicatedDataException(LOGIN_ALREADY_EXIST);
        }
    }

    private void checkUniqueEmail(String email) {
        boolean isUserEmailAlreadyExist = userStorage.findAll().stream()
                .anyMatch(existingUser -> Objects.equals(existingUser.getEmail(), email));
        if (isUserEmailAlreadyExist) {
            log.error("Email already exists: {}", email);
            throw new DuplicatedDataException(EMAIL_ALREADY_EXIST);
        }
    }
}
