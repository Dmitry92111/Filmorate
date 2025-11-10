package ru.yandex.practicum.filmorate.storage.db.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.DbFilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbFilmStorageIntegrationTest {

    private final DbFilmStorage filmStorage;

    @Test
    void testSaveFindUpdateDeleteFilm() {
        // Создаем фильм
        Film film = new Film("Начало", "Фильм о сновидениях", LocalDate.of(2010, 7, 16), 148L);
        film.setMpa(new Mpa(3L, MpaRating.PG_13));
        Film savedFilm = filmStorage.save(film);

        // Проверка, что ID присвоен
        assertThat(savedFilm.getId()).isNotNull();

        // Чтение по ID
        Optional<Film> foundFilmOpt = filmStorage.findById(savedFilm.getId());
        assertThat(foundFilmOpt).isPresent();
        Film foundFilm = foundFilmOpt.get();
        assertThat(foundFilm.getName()).isEqualTo("Начало");
        assertThat(foundFilm.getDescription()).isEqualTo("Фильм о сновидениях");
        assertThat(foundFilm.getDuration()).isEqualTo(148L);
        assertThat(foundFilm.getReleaseDate()).isEqualTo(LocalDate.of(2010, 7, 16));

        // Обновление фильма
        foundFilm.setName("Inception");
        foundFilm.setDescription("Фильм о снах и подсознании");
        filmStorage.update(foundFilm);

        Optional<Film> updatedFilmOpt = filmStorage.findById(foundFilm.getId());
        assertThat(updatedFilmOpt).isPresent();
        Film updatedFilm = updatedFilmOpt.get();
        assertThat(updatedFilm.getName()).isEqualTo("Inception");
        assertThat(updatedFilm.getDescription()).isEqualTo("Фильм о снах и подсознании");

        // Получение всех фильмов
        Collection<Film> allFilms = filmStorage.findAll();
        assertThat(allFilms).isNotEmpty();
        assertThat(allFilms).extracting("id").contains(foundFilm.getId());

        // Удаление фильма
        filmStorage.deleteById(foundFilm.getId());
        Optional<Film> deletedFilmOpt = filmStorage.findById(foundFilm.getId());
        assertThat(deletedFilmOpt).isEmpty();

        // Проверка удаления несуществующего фильма
        assertThrows(RuntimeException.class, () -> filmStorage.deleteById(999L));
    }
}
