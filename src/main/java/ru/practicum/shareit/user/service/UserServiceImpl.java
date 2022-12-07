package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return Optional.ofNullable(userMapper.toUserDto(userRepository.getReferenceById(id)))
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %d not found", id)));
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
        User user = userMapper.toUserEntity(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDto updateUser(UserDto userDto, Long userId) {
        User user = userRepository.getReferenceById(userId);
        user.setName(userDto.getName() != null ? userDto.getName() : user.getName());
        user.setEmail(userDto.getEmail() != null ? this.isEmailExists(userDto.getEmail()) : user.getEmail());
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    private String isEmailExists(String email) {
        boolean isExists = userRepository.findAll().stream()
                .map(User::getEmail)
                .anyMatch(s -> s.equals(email));
        if (isExists) throw new IllegalArgumentException("email already exists");

        return email;
    }
}