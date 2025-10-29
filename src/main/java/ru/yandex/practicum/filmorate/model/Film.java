package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Long id;

    @NotBlank(message = BLANK_OR_NULL_FILM_NAME)
    private String name;

    @Size(max = 200, message = FILM_DESCRIPTION_CANNOT_CONTAIN_MORE_THAT_200_CHARS)
    private String description;

    @Past(message = FILM_RELEASE_DATE_IS_IN_THE_FUTURE)
    private LocalDate releaseDate;

    @Positive(message = FILM_DURATION_CANNOT_BE_NEGATIVE_NUMBER)
    private Long duration;

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
