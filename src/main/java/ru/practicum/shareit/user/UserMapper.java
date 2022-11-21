package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setName(user.getName())
                .setEmail(user.getEmail())
                .setId(user.getId());

        return userDto;
    }
}
