package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Long id;

    @NotBlank(message = BLANK_OR_NULL_FILM_NAME)
    private String name;

    @Size(max = 200, message = FILM_DESCRIPTION_CANNOT_CONTAIN_MORE_THAT_200_CHARS)
    private String description;

    private LocalDate releaseDate;

    @Positive(message = FILM_DURATION_CANNOT_BE_NEGATIVE_NUMBER)
    private Long duration;

    private Set<Long> likedUsersIds;
    private List<Genre> genres;
    private Mpa mpa;

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        likedUsersIds = new HashSet<>();
        genres = new ArrayList<>();
    }
}
