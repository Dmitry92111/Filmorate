package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Set;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    void deleteById(Long id);

    void addGenreToFilm(Long filmId, Genre genre);

    void clearGenresFromFilm(Long filmId);

    Set<Genre> getGenres(Long filmId);

    void setRating(Long filmId, Mpa rating);

    Mpa getRating(Long filmId);

    void loadGenresLikesAndRating(Film film);
}
