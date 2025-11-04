package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {
    private Long id;

    @NotBlank(message = BLANK_OR_NULL_LOGIN)
    private final String login;

    @NotBlank(message = BLANK_OR_NULL_EMAIL)
    @Email(message = INCORRECT_EMAIL)
    private String email;

    private String name;

    @Past(message = USER_BIRTHDAY_IS_IN_THE_FUTURE)
    private LocalDate birthday;

    @JsonCreator
    public User(
            @JsonProperty("login") String login,
            @JsonProperty("email") String email,
            @JsonProperty("name") String name,
            @JsonProperty("birthday") LocalDate birthday
    ) {
        this.login = login;
        this.email = email;
        this.name = name;
        this.birthday = birthday;
    }
}
