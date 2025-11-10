package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;
import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.EMAIL_ALREADY_EXIST;

@Slf4j
@Service
public class UserService {
    UserStorage userStorage;
    FriendshipStorage friendshipStorage;

    public UserService(@Qualifier("DbUserStorage") UserStorage userStorage,
                       FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
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


    public Friendship sendFriendRequest(Long userId, Long friendId) {
        log.info("User {} is sending a friend request to {}", userId, friendId);

        User user = findById(userId);
        User friend = findById(friendId);

        boolean alreadySent = friendshipStorage.exists(user.getId(), friend.getId());
        if (alreadySent) {
            log.warn("Friend request already exists from {} to {}", userId, friendId);
            return friendshipStorage.find(userId, friendId)
                    .orElse(new Friendship(userId, friendId, FriendshipStatus.UNCONFIRMED));
        }

        Friendship friendship = new Friendship(userId, friendId, FriendshipStatus.UNCONFIRMED);
        friendshipStorage.add(friendship);

        log.info("Friend request sent from {} to {}", userId, friendId);
        return friendship;
    }

    public void confirmFriendRequest(Long userId, Long requesterId) {
        log.info("User {} is confirming friendship with {}", userId, requesterId);

        Optional<Friendship> existingRequest = friendshipStorage.find(requesterId, userId);

        if (existingRequest.isPresent()) {
            Friendship friendship = existingRequest.get();
            friendship.setStatus(FriendshipStatus.CONFIRMED);
            friendshipStorage.update(friendship);
            log.info("Friendship between {} and {} confirmed", requesterId, userId);
        } else {
            friendshipStorage.add(new Friendship(userId, requesterId, FriendshipStatus.UNCONFIRMED));
            log.info("No existing request from {}, created new unconfirmed friendship", requesterId);
        }
    }

    public void removeFromFriends(Long userId, Long friendId) {
        log.info("Trying to remove friendship between users {} and {}", userId, friendId);
        User user = findById(userId);
        User friend = findById(friendId);

        friendshipStorage.remove(user.getId(), friend.getId());

        log.info("Users {} and {} are not friends", userId, friendId);
    }

    public Collection<User> getListOfFriends(Long userId) {
        User user = findById(userId);
        return friendshipStorage.findAllFriends(user.getId()).stream()
                .map(f -> findById(f.getFriendId()))
                .toList();
    }

    public Collection<User> getListOfCommonFriends(Long userId, Long anotherUserId) {
        log.info("Trying to get list of common friends of users {} and {}", userId, anotherUserId);

        Set<Long> userFriends = friendshipStorage.findAllFriends(userId).stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        Set<Long> anotherUserFriends = friendshipStorage.findAllFriends(anotherUserId).stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        Collection<User> commonFriends = userFriends.stream()
                .filter(anotherUserFriends::contains)
                .map(this::findById)
                .toList();
        log.info("Common friends of users {} and {} have been found", userId, anotherUserId);
        return commonFriends;
    }

    public void deleteUserById(Long userId) {
        log.info("Trying to delete user with id={}", userId);
        User user = findById(userId);
        userStorage.deleteById(user.getId());
        log.info("User with id={} has been deleted", userId);
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
