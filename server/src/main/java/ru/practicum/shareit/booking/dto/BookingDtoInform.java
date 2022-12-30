package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BookingDtoInform {
    private Long id;
    private Long bookerId;
}
