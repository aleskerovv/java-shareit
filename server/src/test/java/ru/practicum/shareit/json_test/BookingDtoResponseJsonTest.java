package ru.practicum.shareit.json_test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoResponseJsonTest {
    @Autowired
    private JacksonTester<BookingDtoResponse> json;
    @Autowired
    private JacksonTester<UserDto> uJson;
    @Autowired
    private JacksonTester<ItemDto> iJson;

    @Test
    @SneakyThrows
    void testBookingDtoResponse() {
        BookingDtoResponse dto = new BookingDtoResponse();
        UserDto userDto = new UserDto();
        ItemDto itemDto = new ItemDto();

        dto.setStartDate(LocalDateTime.now())
                .setEndDate(LocalDateTime.now().plusDays(15))
                .setBooker(userDto)
                .setItem(itemDto)
                .setStatus(BookStatus.WAITING);

        JsonContent<BookingDtoResponse> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathValue("$.item").isNotNull();
        assertThat(result).extractingJsonPathValue("$.booker").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();

    }
}
