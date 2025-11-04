package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.StringUtils;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;

@Slf4j
@Component
public class ValidationService {
    private static final LocalDate MIN_DATE_OF_BIRTH = LocalDate.of(1900, 1, 1);
    private static final LocalDate MIN_RELEASE_DATE_OF_FILM = LocalDate.of(1895, 12, 28);


    public void validateUser(User user) {
        if (user == null) {
            log.error("Validation failed: User is null");
            throw new ValidationException(USER_CANNOT_BE_NULL);
        }
        if (user.getId() == null && user.getLogin().contains(" ")) {
            log.error("Validation failed: Login contains spaces ({})", user.getLogin());
            throw new ValidationException(INCORRECT_LOGIN_CONTAINS_SPACES);
        }
        if (user.getBirthday() != null && user.getBirthday().isBefore(MIN_DATE_OF_BIRTH)) {
            log.error("Validation failed: Birthday {} is before minimum allowed date", user.getBirthday());
            throw new ValidationException(USER_BIRTHDAY_IS_BEFORE_MIN_DATE);
        }
        if (StringUtils.isNullOrBlank(user.getName())) {
            log.info("User name is blank");
            user.setName(user.getLogin());
        }
    }

    public void validateFilm(Film film) {
        if (film == null) {
            log.error("Validation failed: Film is null");
            throw new ValidationException(FILM_CANNOT_BE_NULL);
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE_OF_FILM)) {
            log.error("Validation failed: Film release date {} is before minimum allowed date", film.getReleaseDate());
            throw new ValidationException(FILM_RELEASE_DATE_IS_BEFORE_MIN_DATE);
        }
    }
}
