package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingDtoInform;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class ItemDtoResponse {
    private Long id;
    @NotBlank(groups = Create.class, message = "'name' can not be blank")
    private String name;
    @NotNull(groups = Create.class, message = "'description' can not be null")
    private String description;
    @NotNull(groups = Create.class, message = "'available' can not be null")
    private Boolean available;
    private UserDto owner;
    private BookingDtoInform lastBooking;
    private BookingDtoInform nextBooking;
    private Set<CommentDtoResponse> comments = new HashSet<>();
}
