package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class DbFriendshipStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(Friendship friendship) {
        String sql = "INSERT INTO friendships (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, friendship.getUserId(), friendship.getFriendId(), friendship.getStatus().name());
    }

    @Override
    public void update(Friendship friendship) {
        String sql = "UPDATE friendships SET status = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, friendship.getStatus().name(), friendship.getUserId(), friendship.getFriendId());
    }

    @Override
    public boolean remove(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        int affected = jdbcTemplate.update(sql, userId, friendId);
        return affected > 0;
    }

    @Override
    public boolean exists(Long userId, Long friendId) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return count != null && count > 0;
    }

    @Override
    public Set<Friendship> findAllFriends(Long userId) {
        String sql = "SELECT * FROM friendships WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) ->
                new Friendship(
                        rs.getLong("user_id"),
                        rs.getLong("friend_id"),
                        FriendshipStatus.valueOf(rs.getString("status"))
                ), userId));
    }

    @Override
    public Optional<Friendship> find(Long userId, Long friendId) {
        String sql = "SELECT * FROM friendships WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        new Friendship(rs.getLong("user_id"), rs.getLong("friend_id"), FriendshipStatus.valueOf(rs.getString("status"))),
                userId, friendId
        ).stream().findFirst();
    }
}
