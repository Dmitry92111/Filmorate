package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationForValidUser() {
        User user = new User(
                "login123",
                "user@example.com",
                "Vanya",
                LocalDate.of(1990, 1, 1)
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "Валидация должна пройти для корректных данных");
    }

    @Test
    void shouldFailWhenLoginIsBlank() {
        User user = new User(
                "   ",
                "user@example.com",
                "Vanya",
                LocalDate.of(1990, 1, 1)
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    void shouldFailWhenEmailIsBlank() {
        User user = new User(
                "login123",
                "   ",
                "Vanya",
                LocalDate.of(1990, 1, 1)
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        User user = new User(
                "login123",
                "invalid-email",
                "Vanya",
                LocalDate.of(1990, 1, 1)
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void shouldFailWhenBirthdayIsInFuture() {
        User user = new User(
                "login123",
                "user@example.com",
                "Vanya",
                LocalDate.now().plusDays(1)
        );

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }
}