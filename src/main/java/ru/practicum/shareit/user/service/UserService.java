package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto getUserById(Long id);

    List<UserDto> getUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long id);

    void deleteById(Long id);
}
