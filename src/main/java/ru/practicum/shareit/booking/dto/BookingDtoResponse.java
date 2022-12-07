package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.enums.BookStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Accessors(chain = true)
public class BookingDtoResponse {
    private Long id;
    @JsonProperty(value = "start")
    private LocalDateTime startDate;
    @JsonProperty(value = "end")
    private LocalDateTime endDate;
    private ItemDto item;
    private UserDto booker;
    private BookStatus status;
}
