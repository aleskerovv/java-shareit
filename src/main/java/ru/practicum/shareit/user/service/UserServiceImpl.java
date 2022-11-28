package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserById(Long id) {
        return userMapper.toUserDto(userRepository.getById(id));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.getAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toUserEntity(userDto);
        return userMapper.toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userMapper.toUserEntity(userDto);
        return userMapper.toUserDto(userRepository.update(user, userId));
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
