package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        try {
            return userMapper.toUserDto(userRepository.getReferenceById(id));
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException(String.format("User with id %d not found", id));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ConstraintViolationException.class)
    public UserDto createUser(UserDto userDto) {
        log.info("creating new user");
        User user = userMapper.toUserEntity(userDto);

        UserDto response = userMapper.toUserDto(userRepository.save(user));
        log.info("created new user: {}", user);
        return response;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDto updateUser(UserDto userDto, Long userId) {
        log.info("updating user with id {}", userId);

        User user = userRepository.getReferenceById(userId);
        user.setName(userDto.getName() != null ? userDto.getName() : user.getName());
        user.setEmail(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail());

        UserDto response = userMapper.toUserDto(userRepository.save(user));
        log.info("updated user with id {}", userId);

        return response;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteById(Long id) {
        this.getUserById(id);
        userRepository.deleteById(id);
        log.info("deleted user with id {}", id);
    }
}