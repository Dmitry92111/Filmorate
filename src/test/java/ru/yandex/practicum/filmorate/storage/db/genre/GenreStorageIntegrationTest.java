package ru.yandex.practicum.filmorate.storage.db.genre;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.DbGenreStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreStorageIntegrationTest {

    private final DbGenreStorage genreStorage;

    @Test
    void testGenres() {
        // Получаем все жанры
        Collection<Genre> genres = genreStorage.findAll();
        assertThat(genres).isNotEmpty();

        // Проверяем жанр по id
        Optional<Genre> genreOpt = genreStorage.findById(1);
        assertThat(genreOpt).isPresent();
        Genre genre = genreOpt.get();
        assertThat(genre.getName()).isNotEmpty();

        // Проверяем несуществующий жанр
        Optional<Genre> genre1 = genreStorage.findById(999);
        Assertions.assertTrue(genre1.isEmpty());
    }
}
