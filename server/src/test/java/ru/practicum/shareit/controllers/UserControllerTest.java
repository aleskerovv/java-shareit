package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityNotFoundException;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(scripts = {"file:src/test/resources/test-schema.sql", "file:src/test/resources/test-data.sql"})
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void creates_newUser_andStatusIs200() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("another user")
                .setEmail("test_user@gmail.com");

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("another user"));
    }

    @Test
    void getUser_byId_andStatusIs200() throws Exception {
        mockMvc.perform(
                        get("/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@email.ru"));
    }

    @Test
    void getUser_byId_andUserNotFound() throws Exception {
        mockMvc.perform(
                        get("/users/15")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof EntityNotFoundException))
                .andExpect(jsonPath("$.error").value("User with id 15 not found"));
    }

    @Test
    @SneakyThrows
    void updateUser_andStatusIsOk() {
        UserDto userDto = new UserDto();
        userDto.setName("Lucifer");

        mockMvc.perform(
                        patch("/users/1")
                                .content(objectMapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@email.ru"))
                .andExpect(jsonPath("$.name").value("Lucifer"));
    }

    @Test
    @SneakyThrows
    void getAllUsers_andStatusIsOk() {
        mockMvc.perform(
                        get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)));
    }

    @Test
    @SneakyThrows
    void deleteUserId1() {
        mockMvc.perform(
                        delete("/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk());
    }
}