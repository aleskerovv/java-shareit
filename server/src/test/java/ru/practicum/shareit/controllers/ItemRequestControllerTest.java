package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @Test
    @SneakyThrows
    void createNewRequest_andStatusIsOk() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");

        ItemRequestDtoResponse response = new ItemRequestDtoResponse();
        response.setDescription("description");
        when(requestService.createItemRequest(itemRequestDto, 1L)).thenReturn(response);

        String result = mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header(USER_ID, 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(response), result);
        verify(requestService).createItemRequest(itemRequestDto, 1L);
    }

    @Test
    @SneakyThrows
    void getUsersRequests_andStatusIsOk() {
        mockMvc.perform(get("/requests")
                .header(USER_ID, 1L))
                .andExpect(status().isOk());

        verify(requestService).findOwnRequests(1L);
    }

    @Test
    @SneakyThrows
    void getRequestById_andStatusIsOk() {
        long requestId = 1L;

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID, 3L))
                .andExpect(status().isOk());

        verify(requestService).getById(requestId, 3L);
    }

    @Test
    @SneakyThrows
    void getAllRequests_andStatusIsOk() {
        long userId = 1L;
        int from = 0;
        int size = 5;

        mockMvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(requestService).getAllRequests(userId, from, size);
    }
}