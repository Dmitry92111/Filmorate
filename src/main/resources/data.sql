MERGE INTO mpa_ratings (name) KEY (name) VALUES ('G');
MERGE INTO mpa_ratings (name) KEY (name) VALUES ('PG');
MERGE INTO mpa_ratings (name) KEY (name) VALUES ('PG_13');
MERGE INTO mpa_ratings (name) KEY (name) VALUES ('R');
MERGE INTO mpa_ratings (name) KEY (name) VALUES ('NC_17');

MERGE INTO genres (genre_id, name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, name) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, name) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, name) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, name) VALUES (6, 'Боевик');