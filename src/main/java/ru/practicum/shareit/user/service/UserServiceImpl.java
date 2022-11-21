package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public UserDto getUserById(Long id) {
        return userDao.getById(id);
    }

    @Override
    public List<UserDto> getUsers() {
        return userDao.getAll();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return userDao.create(userDto);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        return userDao.update(userDto, id);
    }

    @Override
    public void deleteById(Long id) {
        userDao.deleteById(id);
    }
}
