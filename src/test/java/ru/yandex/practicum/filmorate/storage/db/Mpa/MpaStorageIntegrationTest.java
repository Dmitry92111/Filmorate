package ru.yandex.practicum.filmorate.storage.db.Mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;


import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaStorageIntegrationTest {

    private final MpaStorage mpaStorage;

    @Test
    void testMpaRatings() {
        // Получаем все рейтинги
        Collection<Mpa> ratings = mpaStorage.findAll();
        assertThat(ratings).isNotEmpty();
        assertThat(ratings).hasSize(5);

        // Проверка рейтинга по id
        Optional<Mpa> ratingOpt = mpaStorage.findById(1);
        assertThat(ratingOpt).isPresent();
        Mpa rating = ratingOpt.get();
        assertThat(rating.getName()).isNotNull();

        // Проверка несуществующего рейтинга
        Optional<Mpa> rating1 = mpaStorage.findById(999);
        Assertions.assertTrue(rating1.isEmpty());
    }
}
