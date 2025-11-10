package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundInDatabaseException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;

@Repository
@Qualifier("DbFilmStorage")
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new NotFoundInDatabaseException(GENERATED_FILM_ID_NOT_FOUND);
        }
        film.setId(key.longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );
        return film;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, id).stream().findFirst();
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);

        if (rowsDeleted == 0) {
            throw new NotFoundInDatabaseException(FILM_NOT_FOUND_IN_DATABASE + " (" + id + ")");
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration")
        );
        film.setId(rs.getLong("film_id"));

        String sqlLikes = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Long> likedUsers = jdbcTemplate.query(sqlLikes,
                (rs2, rowNum2) -> rs2.getLong("user_id"),
                film.getId());
        film.getLikedUsersIds().addAll(likedUsers);
        return film;
    }

    @Override
    public void addGenreToFilm(Long filmId, Genre genre) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genre.getId());
    }

    @Override
    public void clearGenresFromFilm(Long filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public Set<Genre> getGenres(Long filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?" +
                "ORDER BY g.genre_id";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getLong("genre_id"),
                rs.getString("name")
        ), filmId));
    }

    @Override
    public void setRating(Long filmId, Mpa mpa) {
        String sql = "UPDATE films SET mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, mpa != null ? mpa.getId() : null, filmId);
    }

    @Override
    public Mpa getRating(Long filmId) {
        String sql = "SELECT m.mpa_id, m.name FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.mpa_id " +
                "WHERE f.film_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            int id = rs.getInt("mpa_id");
            if (rs.wasNull()) {
                return null; // если фильм без рейтинга
            }
            String nameStr = rs.getString("name");
            MpaRating rating = MpaRating.valueOf(nameStr);
            return new Mpa((long) id, rating);
        }, filmId);
    }

    public void loadGenresLikesAndRating(Film film) {
        film.setGenres(new ArrayList<>(getGenres(film.getId())));
        film.setMpa(getRating(film.getId()));
        film.setLikedUsersIds(new HashSet<>(
                jdbcTemplate.queryForList("SELECT user_id FROM film_likes WHERE film_id = ?", Long.class, film.getId())
        ));
    }
}