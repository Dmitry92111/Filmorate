package ru.yandex.practicum.filmorate.storage.db.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.DbUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbUserStorageIntegrationTest {

    private final DbUserStorage userStorage;

    @Test
    void testSaveFindUpdateDeleteUser() {
        //Создаем пользователя
        User user = new User("ivan123", "ivan@example.com", "Иван Иванов", LocalDate.of(1990, 5, 20));
        User savedUser = userStorage.save(user);

        assertThat(savedUser.getId()).isNotNull();

        //Чтение по ID
        Optional<User> foundUserOpt = userStorage.findById(savedUser.getId());
        assertThat(foundUserOpt).isPresent();
        User foundUser = foundUserOpt.get();
        assertThat(foundUser.getLogin()).isEqualTo("ivan123");
        assertThat(foundUser.getEmail()).isEqualTo("ivan@example.com");
        assertThat(foundUser.getName()).isEqualTo("Иван Иванов");

        //Обновление пользователя
        foundUser.setName("Иван Петров");
        foundUser.setEmail("ivan.petrov@example.com");
        userStorage.update(foundUser);

        Optional<User> updatedUserOpt = userStorage.findById(foundUser.getId());
        assertThat(updatedUserOpt).isPresent();
        User updatedUser = updatedUserOpt.get();
        assertThat(updatedUser.getName()).isEqualTo("Иван Петров");
        assertThat(updatedUser.getEmail()).isEqualTo("ivan.petrov@example.com");

        //Получение всех пользователей
        Collection<User> allUsers = userStorage.findAll();
        assertThat(allUsers).isNotEmpty();
        assertThat(allUsers).extracting("id").contains(foundUser.getId());

        //Удаление пользователя
        userStorage.deleteById(foundUser.getId());
        Optional<User> deletedUserOpt = userStorage.findById(foundUser.getId());
        assertThat(deletedUserOpt).isEmpty();

    }
}