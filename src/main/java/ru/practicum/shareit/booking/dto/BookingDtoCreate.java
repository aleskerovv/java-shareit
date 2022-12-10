package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BookingDtoCreate {
    @NotNull
    private Long itemId;
    @NotNull
    @FutureOrPresent(message = "'start' must be future or present date")
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
}
