package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.persistence.EntityNotFoundException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(scripts = {"file:src/test/resources/schema.sql", "file:src/test/resources/data.sql"})
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                        instanceof EntityNotFoundException))
                .andExpect(jsonPath("$.error").value("Unable to find ru.practicum.shareit.user.model.User with id 125"));
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
                .andExpect(jsonPath("$.error").value("Required request header " +
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
                .andExpect(jsonPath("$.error").value("'name' can not be blank"));
    }

    @Test
    void getItem_byId1() throws Exception {
        mockMvc.perform(
                get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(jsonPath("$.name").value("item1"));
    }

    @Test
    void getAllItems_byUser() throws Exception {
        mockMvc.perform(
                get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("item1")))
                .andExpect(jsonPath("$[1].name", is("item2")));
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
                .andExpect(jsonPath("$.error").value("You have no access to edit this item"));
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
                        .header("X-Sharer-User-Id", 2L)
        ).andExpect(status().isOk());

        mockMvc.perform(
                        get("/items/search?text=кос")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }
}
