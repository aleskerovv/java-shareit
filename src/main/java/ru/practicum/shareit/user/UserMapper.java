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

    public static User toUserEntity(UserDto userDto) {
        User user = new User();

        user.setEmail(userDto.getEmail())
                .setName(userDto.getName());

        return user;
    }

    public static User toUserEntity(UserDto userDto, Long id) {
        User user = new User();
        user.setName(userDto.getName())
                .setEmail(userDto.getEmail())
                .setId(id);

        return user;
    }
}
