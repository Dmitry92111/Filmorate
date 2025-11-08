package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.ValidationService;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final ValidationService validationService;
    private final FilmService filmService;

    public FilmController(ValidationService validationService, FilmService filmService) {
        this.validationService = validationService;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable long id) {
        return filmService.findById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validationService.validateFilm(film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        validationService.validateFilm(film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id,
                        @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id,
                           @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostLikedFilms(count);
    }

    @DeleteMapping("{filmId}")
    public void deleteById(@PathVariable long filmId) {
        filmService.deleteFilmById(filmId);
    }

    @PutMapping("/{id}/genres")
    public void setGenres(@PathVariable long id, @RequestBody Set<Genre> genres) {
        filmService.updateGenres(id, genres);
    }

    @GetMapping("/{id}/genres")
    public Set<Genre> getGenres(@PathVariable long id) {
        return filmService.getGenres(id);
    }

    @PutMapping("/{id}/rating")
    public void setRating(@PathVariable long id, @RequestBody MpaRating rating) {
        filmService.updateRating(id, rating);
    }

    @GetMapping("/{id}/rating")
    public Mpa getRating(@PathVariable long id) {
        return filmService.getRating(id);
    }
}
