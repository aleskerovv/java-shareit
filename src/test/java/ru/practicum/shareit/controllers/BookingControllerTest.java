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
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exceptions.BookingsAccessException;
import ru.practicum.shareit.exceptions.IncorrectStateException;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Sql(scripts = {"file:src/test/resources/schema.sql", "file:src/test/resources/data.sql"})
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createNewBooking_andStatus_isOk() throws Exception {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setItemId(1L)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(15));

        mockMvc.perform(
                post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L)
        ).andExpect(status().isOk());
    }

    @Test
    void create_newBooking_whenStartInPast() throws Exception {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setItemId(1L)
                .setStart(LocalDateTime.now().minusDays(1))
                .setEnd(LocalDateTime.now().plusDays(15));

        mockMvc.perform(
                post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void create_newBooking_whenEndInPast() throws Exception {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setItemId(1L)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().minusDays(15));

        mockMvc.perform(
                post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void create_newBooking_whenItemIsUnavailable() throws Exception {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate();
        bookingDtoCreate.setItemId(2L)
                .setStart(LocalDateTime.now().plusDays(1))
                .setEnd(LocalDateTime.now().plusDays(15));

        mockMvc.perform(
                post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDtoCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void getBookings_byOwner_inFuture() throws Exception {
        mockMvc.perform(
                        get("/bookings/owner?state=FUTURE")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(4)));
    }

    @Test
    void getBookings_byOwner_inPast() throws Exception {
        mockMvc.perform(
                        get("/bookings/owner?state=PAST")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    void fail_whenChangingStatus_forSameState() throws Exception {
        mockMvc.perform(
                patch("/bookings/1?approved=false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        ).andExpect(status().isOk());

        mockMvc.perform(
                        patch("/bookings/1?approved=false")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                ).andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof IncorrectStateException))
                .andExpect(jsonPath("$.error").value("Booking is already REJECTED"));
    }

    @Test
    void fail_whenSetApprove_fromUserIsNotOwner() throws Exception {
        mockMvc.perform(
                        patch("/bookings/1?approved=false")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 2L)
                ).andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof BookingsAccessException))
                .andExpect(jsonPath("$.error").value("You have no access to edit this booking"));
    }

    @Test
    @SneakyThrows
    void getAllBookingByStateByUser_andReturn_2() {
        mockMvc.perform(
                get("/bookings?from=0&size=5")
                        .header("X-Sharer-User-Id", 2L)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));
    }
}