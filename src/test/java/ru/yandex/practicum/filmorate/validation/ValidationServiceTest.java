package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }

    // ---------- USER VALIDATION TESTS ----------

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        User user = new User("bad login", "email@mail.com", "John", LocalDate.of(1990, 1, 1));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validationService.validateUser(user)
        );

        assertTrue(ex.getMessage().contains("пробел"), "Ожидается ошибка о пробелах в логине");
    }

    @Test
    void shouldThrowExceptionWhenBirthdayBefore1900() {
        User user = new User("login", "email@mail.com", "John", LocalDate.of(1800, 1, 1));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validationService.validateUser(user)
        );

        assertTrue(ex.getMessage().contains("1900"), "Ожидается сообщение о минимальной дате рождения");
    }

    @Test
    void shouldSetLoginAsNameWhenNameIsBlank() {
        User user = new User("login123", "email@mail.com", "   ", LocalDate.of(1990, 1, 1));

        assertDoesNotThrow(() -> validationService.validateUser(user));
        assertEquals("login123", user.getName(), "Имя должно быть заменено логином");
    }

    @Test
    void shouldPassValidationForValidUser() {
        User user = new User("login123", "email@mail.com", "Vanya", LocalDate.of(1990, 1, 1));

        assertDoesNotThrow(() -> validationService.validateUser(user));
    }

    // ---------- FILM VALIDATION TESTS ----------

    @Test
    void shouldPassValidationForValidFilm() {
        Film film = new Film("Film1", "desc", LocalDate.of(2000, 1, 1), 120);

        assertDoesNotThrow(() -> validationService.validateFilm(film));
    }

    @Test
    void shouldThrowExceptionWhenFilmDateBefore1895() {
        Film film = new Film("Film1", "desc", LocalDate.of(1800, 1, 1), 100);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validationService.validateFilm(film)
        );

        assertTrue(ex.getMessage().contains("1895"), "Ожидается сообщение о минимальной дате релиза");
    }
}
