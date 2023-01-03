package ru.practicum.shareit.integrations;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.PaginationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"file:src/test/resources/test-schema.sql", "file:src/test/resources/test-data.sql"})
class ItemServiceImplIT {
    private final ItemService itemService;

    @Test
    void test_getUsersItems() {
        List<ItemDtoResponse> response = itemService.getItemsByOwnerId(1L, 0, 5);

        assertThat(response.size(), equalTo(2));
        assertThat(response.get(0).getLastBooking(), notNullValue());
    }

    @Test
    void test_createItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("new test")
                .setAvailable(true)
                .setDescription("new test desc");

        ItemDtoResponse response = itemService.create(itemDto, 1L);

        assertThat(response.getOwner().getId(), equalTo(1L));
        assertThat(response.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void test_whenWrongPaginationParams_thenThrowsException() {
        final PaginationException ex = assertThrows(
                PaginationException.class,
                () -> itemService.getItemsByOwnerId(1L, -1, 2)
        );

        assertThat("Page index must not be less than zero", equalTo(ex.getMessage()));
    }

    @Test
    void test_whenWrongPaginationParamsOfSize_thenThrowsException() {
        final PaginationException ex = assertThrows(
                PaginationException.class,
                () -> itemService.getItemsByOwnerId(1L, 1, 0)
        );

        assertThat("Page size must not be less than one", equalTo(ex.getMessage()));
    }

    @Test
    void test_findItemNotPresented() {
        final EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.getById(25L, 1L)
        );

        assertThat("Item with id 25 not found", equalTo(ex.getMessage()));
    }
}
