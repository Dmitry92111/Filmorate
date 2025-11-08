package ru.yandex.practicum.filmorate.messages;

public class ExceptionMessages {
    public static final String BLANK_OR_NULL_EMAIL = "Поле E-mail не может быть пустым";
    public static final String BLANK_OR_NULL_LOGIN = "Поле Login не может быть пустым";
    public static final String LOGIN_ALREADY_EXIST = "Пользователь с таким Логином уже существует";
    public static final String EMAIL_ALREADY_EXIST = "Пользователь с таким E-mail уже существует";
    public static final String USER_BIRTHDAY_IS_BEFORE_MIN_DATE = "Дата рождения не может быть ранее 01.01.1900";
    public static final String USER_BIRTHDAY_IS_IN_THE_FUTURE = "Дата рождения не может быть в будущем";
    public static final String ID_CANNOT_BE_NULL = "Id должен быть указан";
    public static final String USER_BY_ID_NOT_FOUND = "Пользователь с указанным id не найден";
    public static final String FILM_BY_ID_NOT_FOUND = "Фильм с указанным id не найден";
    public static final String INCORRECT_EMAIL = "Указан E-mail в некорректном формате";
    public static final String INCORRECT_LOGIN_CONTAINS_SPACES = "Login не может содержать пробелы";
    public static final String USER_CANNOT_BE_NULL = "Пользователь не может быть null";
    public static final String BLANK_OR_NULL_FILM_NAME = "Название фильма не может быть пустым";
    public static final String FILM_DESCRIPTION_CANNOT_CONTAIN_MORE_THAT_200_CHARS
            = "Описание фильма не может содержать более 200 символов";
    public static final String FILM_DURATION_CANNOT_BE_NEGATIVE_NUMBER
            = "Продолжительность фильма не может быть отрицательной";
    public static final String FILM_CANNOT_BE_NULL = "Фильм не может быть null";
    public static final String FILM_RELEASE_DATE_IS_BEFORE_MIN_DATE
            = "Дата выхода фильма не может быть ранее 28.12.1895";
    public static final String FILM_NOT_FOUND_IN_DATABASE = "Фильм не найден в базе данных";
    public static final String GENRE_BY_ID_NOT_FOUND = "Жанр с указанным id не найден";
    public static final String MPA_BY_ID_NOT_FOUND = "Рейтинг с указанным id не найден";
    public static final String GENERATED_USER_ID_NOT_FOUND = "Не найден сгенерированный ID пользователя";
    public static final String GENERATED_FILM_ID_NOT_FOUND = "Не найден сгенерированный ID фильма";

}
