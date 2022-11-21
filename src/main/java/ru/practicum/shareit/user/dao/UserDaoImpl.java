package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.IncorrectEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> userStorage = new HashMap<>();
    private Long id = 1L;
    private static final String USER_NOT_FOUND = "User with id %d not found";

    @Override
    public UserDto getById(Long id) {
        return userStorage.values()
                .stream()
                .filter(user -> user.getId().equals(id))
                .findAny()
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, id)));
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.values()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto userDto) {
        User user = new User();

        isEmailExists(userDto.getEmail());

        user.setId(this.incrementId())
                .setEmail(userDto.getEmail())
                .setName(userDto.getName());
        userStorage.put(user.getId(), user);

        log.info("Created new user. UserId: {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        if (!userStorage.containsKey(id)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, id));
        }

        User user = userStorage.get(id);

        user.setName(userDto.getName() != null ? userDto.getName() : user.getName());

        user.setEmail(userDto.getEmail() != null ? this.updateEmail(userDto.getEmail()) : user.getEmail());

        userStorage.put(user.getId(), user);

        log.info("Updated user with ID {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleted user with ID {}", id);
        userStorage.remove(id);
    }

    private void isEmailCorrect(String email) {
        String regexPattern = "^(.+)@(\\S+)$";
        boolean isCorrect = Pattern.compile(regexPattern)
                .matcher(email)
                .matches();
        if(!isCorrect) throw new IncorrectEmailException("email must be in email format: user@user.com");
    }

    private void isEmailExists(String email) {
        boolean isExists = userStorage.values()
                .stream()
                .map(User::getEmail)
                .anyMatch(s -> s.equals(email));
        if(isExists) throw new IllegalArgumentException("email already exists");
    }

    private String updateEmail(String email) {
        isEmailCorrect(email);
        isEmailExists(email);

        return email;
    }

    private long incrementId() {
        return id++;
    }
}
