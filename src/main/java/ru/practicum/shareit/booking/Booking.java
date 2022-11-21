package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.enums.BookStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Accessors(chain = true)
public class Booking {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Item item;
    private User booker;
    private BookStatus status;

}

