package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;

@Slf4j
@Service
public class FilmService {
    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film addFilm(Film film) {
        log.info("Adding new film: {}", film);
        film.setId(idCounter.incrementAndGet());
        films.put(film.getId(), film);
        log.info("Film added successfully with id={}", film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        log.info("Updating film: {}", film);
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.error("Film update failed: ID not found ({})", film.getId());
            throw new NotFoundException(ID_NOT_FOUND + " (" + film.getId() + ")");
        }
        films.put(film.getId(), film);
        log.info("Film updated successfully with id={}", film.getId());
        return film;
    }
}
