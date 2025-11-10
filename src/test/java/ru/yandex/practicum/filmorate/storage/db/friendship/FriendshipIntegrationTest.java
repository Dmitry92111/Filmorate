package ru.yandex.practicum.filmorate.storage.db.friendship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FriendshipIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendshipStorage friendshipStorage;

    private User userA;
    private User userB;

    @BeforeEach
    public void setup() {
        // Создание пользователей
        userA = userService.addUser(new User("userA", "a@example.com", "User A", LocalDate.of(1990, 1, 1)));
        userB = userService.addUser(new User("userB", "b@example.com", "User B", LocalDate.of(1991, 2, 2)));
    }

    @Test
    public void testFriendshipLifecycle() {
        // Добавление запроса
        friendshipStorage.add(new Friendship(userA.getId(), userB.getId(), FriendshipStatus.UNCONFIRMED));
        assertTrue(friendshipStorage.exists(userA.getId(), userB.getId()));

        // Проверка статуса
        Friendship request = friendshipStorage.find(userA.getId(), userB.getId()).orElseThrow();
        assertEquals(FriendshipStatus.UNCONFIRMED, request.getStatus());

        // Подтверждение запроса
        request.setStatus(FriendshipStatus.CONFIRMED);
        friendshipStorage.update(request);

        Friendship confirmed = friendshipStorage.find(userA.getId(), userB.getId()).orElseThrow();
        assertEquals(FriendshipStatus.CONFIRMED, confirmed.getStatus());

        // Получение списка друзей
        Set<Friendship> friendsOfA = friendshipStorage.findAllFriends(userA.getId());
        assertEquals(1, friendsOfA.size());
        assertTrue(friendsOfA.stream().anyMatch(f -> f.getFriendId().equals(userB.getId())));

        // Удаление дружбы
        boolean removed = friendshipStorage.remove(userA.getId(), userB.getId());
        assertTrue(removed);
        assertFalse(friendshipStorage.exists(userA.getId(), userB.getId()));
    }
}
