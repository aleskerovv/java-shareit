package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> userStorage = new HashMap<>();
    private Long id = 1L;
    private static final String USER_NOT_FOUND = "User with id %d not found";

    @Override
    public User getById(Long id) {
        if (!userStorage.containsKey(id))
            throw new NotFoundException(String.format(USER_NOT_FOUND, id));

        return userStorage.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User create(User user) {
        this.isEmailExists(user.getEmail());

        user.setId(this.incrementId());
        userStorage.put(user.getId(), user);

        log.info("Created new user. UserId: {}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        User userToUpdate = this.userStorage.get(user.getId());

        userToUpdate.setName(user.getName() != null ? user.getName() : userToUpdate.getName());
        userToUpdate.setEmail(user.getEmail() != null ? this.isEmailExists(user.getEmail()) : userToUpdate.getEmail());

        userStorage.put(userToUpdate.getId(), userToUpdate);

        log.info("Updated user with ID {}", user.getId());
        return userToUpdate;
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleted user with ID {}", id);
        userStorage.remove(id);
    }


    private String isEmailExists(String email) {
        boolean isExists = userStorage.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(s -> s.equals(email));
        if (isExists) throw new IllegalArgumentException("email already exists");

        return email;
    }


    private long incrementId() {
        return id++;
    }
}
