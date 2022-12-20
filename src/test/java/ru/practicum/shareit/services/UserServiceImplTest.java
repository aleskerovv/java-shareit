package ru.practicum.shareit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserServiceImpl userService;
    private final UserMapper mapper = new UserMapperImpl();
    @Mock
    private UserRepository repository;
    User user = new User();

    @BeforeEach
    void init() {
        this.userService = new UserServiceImpl(repository, mapper);
        user.setEmail("j_doe@gmail.com")
                .setName("John Doe");
    }

    @Test
    void getUserById() {
        Mockito.when(repository.getReferenceById(1L))
                .thenReturn(user.setId(1L));

        UserDto userDto = userService.getUserById(1L);

        assertNotNull(userDto);
        assertEquals(userDto.getName(), user.getName());
        Mockito.verify(repository, Mockito.times(1))
                .getReferenceById(1L);
    }

    @Test
    void getUsersList() {
        User foo = new User();
        foo.setEmail("fooo@gmail.com")
                .setName("FooBar");

        List<User> userList = new ArrayList<>();
        userList.add(user.setId(1L));
        userList.add(foo.setId(2L));

        Mockito.when(repository.findAll())
                .thenReturn(userList);

        List<UserDto> userDtoList = userService.getUsers();

        assertEquals(2, userDtoList.size());
        assertEquals(userDtoList.get(0).getEmail(), user.getEmail());
        Mockito.verify(repository, Mockito.times(1))
                .findAll();
    }

    @Test
    void deleteUserById() {
        Mockito.doNothing().when(repository).deleteById(Mockito.anyLong());

        userService.deleteById(1L);

        Mockito.verify(repository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void createUser() {
        Mockito.when(repository.save(user))
                .thenReturn(user.setId(1L));

        UserDto userDto = userService.createUser(mapper.toUserDto(user));

        assertEquals(userDto.getName(), user.getName());
        Mockito.verify(repository, Mockito.times(1))
                .save(user);
    }

    @Test
    void updateUser() {
        user.setName("Updated John Doe");
        Mockito.when(repository.getReferenceById(1L))
                .thenReturn(user);

        Mockito.when(repository.save(user))
                .thenReturn(user.setId(1L));

        UserDto userDto = userService.updateUser(mapper.toUserDto(user), 1L);

        assertEquals("Updated John Doe", userDto.getName());
        Mockito.verify(repository, Mockito.times(1))
                .save(user);
    }
}
