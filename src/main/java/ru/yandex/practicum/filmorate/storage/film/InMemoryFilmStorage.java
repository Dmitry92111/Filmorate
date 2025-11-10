package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film save(Film film) {
        film.setId(idCounter.incrementAndGet());
        return films.put(film.getId(), film);
    }

    @Override
    public Film update(Film film) {
        return films.put(film.getId(), film);
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void deleteById(Long id) {
        films.remove(id);
    }

    @Override
    public void addGenreToFilm(Long filmId, Genre genre) {
        Film film = films.get(filmId);
        if (film != null) {
            film.getGenres().add(genre);
        }
    }

    @Override
    public void clearGenresFromFilm(Long filmId) {
        Film film = films.get(filmId);
        if (film != null) {
            film.getGenres().clear();
        }
    }

    @Override
    public Set<Genre> getGenres(Long filmId) {
        Film film = films.get(filmId);
        if (film != null) {
            return new HashSet<>(film.getGenres());
        }
        return Collections.emptySet();
    }

    @Override
    public void setRating(Long filmId, Mpa rating) {
        Film film = films.get(filmId);
        if (film != null) {
            film.setMpa(rating);
        }
    }

    @Override
    public Mpa getRating(Long filmId) {
        Film film = films.get(filmId);
        return film != null ? film.getMpa() : null;
    }

    public void loadGenresLikesAndRating(Film film) {
        films.values().forEach(film1 -> film1.setGenres(film.getGenres()));
    }
}
