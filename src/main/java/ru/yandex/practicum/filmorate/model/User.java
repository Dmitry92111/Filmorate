package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static ru.yandex.practicum.filmorate.messages.ExceptionMessages.*;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {
    private Long id;

    @NotBlank(message = BLANK_OR_NULL_LOGIN)
    private String login;

    @NotBlank(message = BLANK_OR_NULL_EMAIL)
    @Email(message = INCORRECT_EMAIL)
    private String email;

    private String name;

    @PastOrPresent(message = USER_BIRTHDAY_IS_IN_THE_FUTURE)
    private LocalDate birthday;

    private final Set<Long> friendsIds;

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
        friendsIds = new HashSet<>();
    }
}
