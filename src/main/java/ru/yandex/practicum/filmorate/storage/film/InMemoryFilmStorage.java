package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film save(Film film) {
        film.setId(idCounter.incrementAndGet());
        return films.put(film.getId(), film);
    }

    public Film update(Film film) {
        return films.put(film.getId(), film);
    }

    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }
}
