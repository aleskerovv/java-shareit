package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserGatewayDto;
import ru.practicum.shareit.validation.Create;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("getting user by id {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(Create.class) @RequestBody UserGatewayDto userDto) {
        log.info("creating new user: {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> updateUser(@Validated @RequestBody UserGatewayDto userDto,
                                             @PathVariable long userId) {
        log.info("updating user with id {}", userId);
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        log.info("deleting user with id {}", userId);
        return userClient.deleteUserById(userId);
    }
}
