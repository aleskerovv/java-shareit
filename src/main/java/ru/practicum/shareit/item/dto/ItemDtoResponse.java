package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingDtoInform;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class ItemDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private BookingDtoInform lastBooking;
    private BookingDtoInform nextBooking;
    private Set<CommentDtoResponse> comments = new HashSet<>();
    private Long requestId;
}
