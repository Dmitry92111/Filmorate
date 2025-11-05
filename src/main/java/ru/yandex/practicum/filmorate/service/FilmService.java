package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final Comparator<Film> amountOfLikesComparator = Comparator.comparingInt(film -> film.getLikedUsersIds().size());

    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        log.info("Adding new film: {}", film);
        filmStorage.save(film);
        log.info("Film added successfully with id={}", film.getId());
        return film;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        log.info("Trying to find film by ID: {}", id);
        if (id == null) {
            log.error("Film search failed, method got null instead of ID number");
            throw new ConditionsNotMetException(ID_CANNOT_BE_NULL);
        }

        return filmStorage.findById(id).
                map(film -> {
                    log.info("Film has been found successfully with id: {}", id);
                    return film;
                })
                .orElseThrow(() -> {
                    log.error("Film search failed: ID not found ({})", id);
                    return new NotFoundException(FILM_BY_ID_NOT_FOUND + " (" + id + ")");
                });
    }

    public Film updateFilm(Film film) {
        log.info("Updating film: {}", film);
        Film existingFilm = findById(film.getId());
        existingFilm.setDuration(film.getDuration());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setName(film.getName());
        log.info("Film updated successfully with id={}", film.getId());
        return existingFilm;
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Trying to add user's like to film {}", filmId);
        Film film = findById(filmId);
        User user = userService.findById(userId);
        film.getLikedUsersIds().add(user.getId());
        log.info("User's like added successfully");
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Trying to remove user's like from film {}", filmId);
        Film film = findById(filmId);
        film.getLikedUsersIds().remove(userId);
        log.info("User's like removed successfully");
    }

    public Collection<Film> getMostLikedFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Размер выборки должен быть положительным числом");
        }
        return filmStorage.findAll().stream()
                .sorted(amountOfLikesComparator.reversed())
                .limit(count).toList();
    }
}
