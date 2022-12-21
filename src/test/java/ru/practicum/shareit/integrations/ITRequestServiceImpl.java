package ru.practicum.shareit.integrations;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.PaginationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ITRequestServiceImpl {
    private final ItemRequestService requestService;

    @Test
    void test_createItemRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("test desc for IT");

        ItemRequestDtoResponse itemRequestDtoResponse = requestService.createItemRequest(requestDto, 1L);

        assertThat(requestDto.getDescription(), equalTo(itemRequestDtoResponse.getDescription()));
    }

    @Test
    void test_getById() {
        ItemRequestDtoResponse response = requestService.getById(1L, 1L);

        assertThat("looking for new hummer", equalTo(response.getDescription()));
    }

    @Test
    void test_findOwnRequest() {
        List<ItemRequestDtoResponse> responses = requestService.findOwnRequests(3L);

        assertThat(responses.get(0).getItems().size(), equalTo(0));
        assertThat(responses.get(0).getDescription(), equalTo("looking for new hummer"));
    }

    @Test
    void test_getAllRequests() {
        List<ItemRequestDtoResponse> responses = requestService.getAllRequests(1L, 0, 10);

        assertThat(responses.get(0).getDescription(), equalTo("looking for new hummer"));
    }

    @Test
    void test_getAllRequests_whenThrowsException() {
        final PaginationException ex = assertThrows(
                PaginationException.class,
                () -> requestService.getAllRequests(1L, -1, 2)
        );

        assertThat("Page index must not be less than zero", equalTo(ex.getMessage()));
    }
}