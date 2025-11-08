CREATE TABLE IF NOT EXISTS mpa_ratings
(
    mpa_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(10) UNIQUE NOT NULL
);


CREATE TABLE IF NOT EXISTS genres
(
    genre_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(50) UNIQUE NOT NULL
);


CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    login    VARCHAR(100) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    name     VARCHAR(255),
    birthday DATE CHECK (birthday <= CURRENT_DATE)
);


CREATE TABLE IF NOT EXISTS films
(
    film_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(200),
    release_date DATE,
    duration     BIGINT CHECK (duration > 0),
    mpa_id       BIGINT,
    CONSTRAINT fk_mpa FOREIGN KEY (mpa_id) REFERENCES mpa_ratings (mpa_id)
);


CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_film_genres_film FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    CONSTRAINT fk_film_genres_genre FOREIGN KEY (genre_id) REFERENCES genres (genre_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS film_likes
(
    film_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (film_id, user_id),
    CONSTRAINT fk_film_likes_film FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE,
    CONSTRAINT fk_film_likes_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS friendships
(
    user_id   BIGINT,
    friend_id BIGINT,
    status    VARCHAR(20) NOT NULL CHECK (status IN ('UNCONFIRMED', 'CONFIRMED')),
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_friendship_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_friendship_friend FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE CASCADE
);