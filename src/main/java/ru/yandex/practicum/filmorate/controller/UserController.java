package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.ValidationService;

import java.util.Collection;


@RestController
@RequestMapping("/users")
public class UserController {
    private final ValidationService validationService;
    private final UserService userService;

    public UserController(ValidationService validationService, UserService userService) {
        this.validationService = validationService;
        this.userService = userService;
    }


    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable long id) {
        return userService.findById(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getListOfFriends(@PathVariable long id) {
        return userService.getListOfFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getListOfCommonFriends(@PathVariable long id,
                                                   @PathVariable long anotherId) {
        return userService.getListOfCommonFriends(id, anotherId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validationService.validateUser(user);
        return userService.addUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        validationService.validateUser(user);
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id,
                          @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id,
                             @PathVariable long friendId) {
        userService.removeFromFriends(id, friendId);
    }
}
