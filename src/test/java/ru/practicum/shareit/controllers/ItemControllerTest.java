package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserDao userDao;

    @BeforeAll
    void create() throws Exception {
        User user = new User();
        user.setId(1L)
                .setName("test user")
                .setEmail("test@user.com");
        userDao.create(user);

        User user2 = new User();
        user2.setId(2L)
                .setName("test 2user")
                .setEmail("test2@user.com");
        userDao.create(user2);

        User user3 = new User();
        user3.setId(3L)
                .setName("test 3user")
                .setEmail("test3@user.com");
        userDao.create(user3);

        ItemDto item = new ItemDto();
        item.setName("Дрель")
                .setDescription("Простая дрель")
                .setAvailable(true);

        mockMvc.perform(
                post("/items")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(status().isOk());
    }

    @Test
    void creates_newItem_andResponseCodeIs200() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Новая Дрель")
                .setDescription("Простая новая дрель")
                .setAvailable(true);

        mockMvc.perform(
                post("/items")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L)
        ).andExpect(status().isOk());
    }

    @Test
    void creates_newItem_andStatusIs404() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Дрель")
                .setDescription("Простая дрель")
                .setAvailable(true);

        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(item))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 125L)
                ).andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof NotFoundException))
                .andExpect(jsonPath("$.message").value("User with id 125 not found"));
    }

    @Test
    void creates_newItem_withoutHeader_andStatusIs400() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Дрель")
                .setDescription("Простая дрель")
                .setAvailable(true);

        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(item))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MissingRequestHeaderException))
                .andExpect(jsonPath("$.message").value("Required request header " +
                        "'X-Sharer-User-Id' for method parameter type Long is not present"));
    }

    @Test
    void creates_newItem_whenName_isNull() throws Exception {
        ItemDto item = new ItemDto();
        item.setDescription("Простая дрель")
                .setAvailable(true);

        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(item))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.message").value("'name' can not be blank"));
    }

    @Test
    void getItem_byId1() throws Exception {
        mockMvc.perform(
                get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void getAllItems_byUser() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Шуруповерт")
                .setDescription("Не простой шуруповерт")
                .setAvailable(true);

        mockMvc.perform(
                post("/items")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(status().isOk());

        mockMvc.perform(
                get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Дрель")))
                .andExpect(jsonPath("$[1].name", is("Шуруповерт")));
    }

    @Test
    void updatesItem_andStatusIsOk() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Дрель")
                .setDescription("Простая дрель")
                .setAvailable(false);

        mockMvc.perform(
                patch("/items/1")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.available").value("false"));

    }

    @Test
    void updatesItem_andThrowsNoAccessExceptions() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Дрель")
                .setDescription("Простая дрель")
                .setAvailable(false);

        mockMvc.perform(
                patch("/items/1")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3L)
        ).andExpect(status().isForbidden())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof NoAccessException))
                .andExpect(jsonPath("$.message").value("You have no access to edit this item"));
    }

    @Test
    void getItems_byParams() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Старая бензопила")
                .setDescription("Старая бензопила")
                .setAvailable(true);

        ItemDto item2 = new ItemDto();
        item2.setName("Бензопила")
                .setDescription("Мощная бензопила")
                .setAvailable(true);

        mockMvc.perform(
                post("/items")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(status().isOk());

        mockMvc.perform(
                post("/items")
                        .content(objectMapper.writeValueAsString(item2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(status().isOk());

        mockMvc.perform(
                        get("/items/search?text=пил")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Старая бензопила"))
                .andExpect(jsonPath("$[1].name").value("Бензопила"));
    }

    @Test
    void getItems_byParam_whenAvailableIsFalse() throws Exception {
        ItemDto item = new ItemDto();
        item.setName("Газонокосилка")
                .setDescription("Современная мощная газонокосилка")
                .setAvailable(false);

        mockMvc.perform(
                post("/items")
                        .content(objectMapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(status().isOk());

        mockMvc.perform(
                        get("/items/search?text=кос")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }
}
