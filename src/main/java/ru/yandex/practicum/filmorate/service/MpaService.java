package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.MPA_BY_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findById(int id) {
        return mpaStorage.findById(id)
                .orElseThrow(() -> new NotFoundException(MPA_BY_ID_NOT_FOUND + " (" + id + ")"));
    }
}
