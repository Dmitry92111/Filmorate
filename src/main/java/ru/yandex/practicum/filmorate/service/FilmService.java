package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final MpaService mpaService;
    private final GenreService genreService;

    public FilmService(@Qualifier("DbFilmStorage") FilmStorage filmStorage,
                       UserService userService,
                       MpaService mpaService,
                       GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public Film addFilm(Film film) {
        log.info("Adding new film: {}", film);
        if (film.getMpa() == null) {
            log.warn("Film {} has no rating, defaulting to G", film.getName());
            film.setMpa(new Mpa(1L, MpaRating.G));
        }

        if (mpaService.findAll().stream().noneMatch(mpa -> mpa.getId().equals(film.getMpa().getId()))) {
            throw new NotFoundException(MPA_BY_ID_NOT_FOUND);
        }
        if (film.getGenres().stream()
                .anyMatch(filmGenre -> genreService.findAll().stream()
                        .noneMatch(dbGenre -> dbGenre.getId().equals(filmGenre.getId())))) {
            throw new NotFoundException(GENRE_BY_ID_NOT_FOUND);
        }

        filmStorage.save(film);

        if (!film.getGenres().isEmpty()) {
            List<Genre> sortedGenres = film.getGenres().stream()
                    .sorted(Comparator.comparingLong(Genre::getId))
                    .toList();

            filmStorage.clearGenresFromFilm(film.getId());
            sortedGenres.forEach(genre -> filmStorage.addGenreToFilm(film.getId(), genre));

            film.getGenres().clear();
            film.getGenres().addAll(sortedGenres);
        }
        log.info("Film added successfully with id={}", film.getId());
        return film;
    }

    public Collection<Film> findAll() {
        log.info("Fetching all films...");
        Collection<Film> films = filmStorage.findAll();
        films.forEach(this::loadGenresLikesAndRating);
        return films;
    }

    public Film findById(Long id) {
        log.info("Trying to find film by ID: {}", id);
        if (id == null) {
            log.error("Film search failed, method got null instead of ID number");
            throw new ConditionsNotMetException(ID_CANNOT_BE_NULL);
        }

        Film film = filmStorage.findById(id)
                .orElseThrow(() -> {
                    log.error("Film search failed: ID not found ({})", id);
                    return new NotFoundException(FILM_BY_ID_NOT_FOUND + " (" + id + ")");
                });

        loadGenresLikesAndRating(film);
        log.info("Film found successfully with id={}", id);
        return film;
    }

    public Film updateFilm(Film film) {
        log.info("Updating film: {}", film);

        Film existingFilm = findById(film.getId());

        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        existingFilm.setMpa(film.getMpa());

        filmStorage.clearGenresFromFilm(existingFilm.getId());
        film.getGenres().forEach(genre -> filmStorage.addGenreToFilm(existingFilm.getId(), genre));

        filmStorage.update(existingFilm);

        log.info("Film updated successfully with id={}", film.getId());
        return existingFilm;
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Trying to add user's like to film {}", filmId);
        Film film = findById(filmId);
        User user = userService.findById(userId);
        film.getLikedUsersIds().add(user.getId());
        filmStorage.update(film);
        log.info("User's like added successfully");
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Trying to remove user's like from film {}", filmId);
        Film film = findById(filmId);
        film.getLikedUsersIds().remove(userId);
        filmStorage.update(film);
        log.info("User's like removed successfully");
    }

    public Collection<Film> getMostLikedFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Размер выборки должен быть положительным числом");
        }


        Collection<Film> allFilms = filmStorage.findAll();

        allFilms.forEach(this::loadGenresAndRating);

        return allFilms.stream()
                .sorted(Comparator
                        .comparingInt((Film f) -> f.getLikedUsersIds().size()).reversed()
                        .thenComparing(Comparator.comparingLong(Film::getId).reversed())
                )
                .limit(count)
                .toList();
    }

    public void deleteFilmById(Long filmId) {
        log.info("Trying to delete film with id={}", filmId);
        Film film = findById(filmId); // проверка, что фильм существует
        filmStorage.deleteById(filmId);
        log.info("Film with id={} has been deleted", film.getId());
    }

    private void loadGenresAndRating(Film film) {
        film.setMpa(filmStorage.getRating(film.getId()));
        film.getGenres().clear();
        film.getGenres().addAll(filmStorage.getGenres(film.getId()));
    }

    public void updateGenres(Long filmId, Set<Genre> genres) {
        Film film = findById(filmId);
        filmStorage.clearGenresFromFilm(filmId);
        film.getGenres().clear();

        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genre -> filmStorage.addGenreToFilm(filmId, genre));
            film.getGenres().addAll(genres);
        }
    }

    public Set<Genre> getGenres(Long filmId) {
        Film film = findById(filmId);
        Set<Genre> genres = filmStorage.getGenres(filmId);
        film.getGenres().clear();
        film.getGenres().addAll(genres);
        return genres;
    }

    public void updateRating(Long filmId, MpaRating rating) {
        Film film = findById(filmId);
        film.setMpa(new Mpa(film.getMpa() != null ? film.getMpa().getId() : null, rating));
        filmStorage.setRating(filmId, film.getMpa());
    }

    public Mpa getRating(Long filmId) {
        Film film = findById(filmId);
        Mpa mpa = filmStorage.getRating(filmId);
        film.setMpa(mpa);
        return mpa;
    }

    private void loadGenresLikesAndRating(Film film) {
        filmStorage.loadGenresLikesAndRating(film);
    }

    /*public void addRandomLikes() {
        Random random = new Random();
        List<Long> userIds = jdbcTemplate.queryForList("SELECT user_id FROM users", Long.class);
        List<Long> filmIds = jdbcTemplate.queryForList("SELECT film_id FROM films", Long.class);

        for (Long filmId : filmIds) {
            Collections.shuffle(userIds, random);
            int likesCount = 1 + random.nextInt(5); // 1–5 лайков
            List<Long> likedUsers = userIds.subList(0, Math.min(likesCount, userIds.size()));

            for (Long userId : likedUsers) {
                // проверяем, есть ли уже лайк от этого пользователя
                Integer existing = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM film_likes WHERE film_id = ? AND user_id = ?",
                        Integer.class,
                        filmId, userId
                );

                if (existing == null || existing == 0) {
                    jdbcTemplate.update(
                            "INSERT INTO film_likes(film_id, user_id) VALUES (?, ?)",
                            filmId, userId
                    );
                }
            }
        }
    }*/
}
