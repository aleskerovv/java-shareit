package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(userDao.getById(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return userDao.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUserEntity(userDto);
        return UserMapper.toUserDto(userDao.create(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        User user = UserMapper.toUserEntity(userDto, id);
        return UserMapper.toUserDto(userDao.update(user));
    }

    @Override
    public void deleteById(Long id) {
        userDao.deleteById(id);
    }
}
