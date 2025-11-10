package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.GENRE_BY_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre findById(int id) {
        return genreStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(GENRE_BY_ID_NOT_FOUND + " (" + id + ")"));
    }
}
