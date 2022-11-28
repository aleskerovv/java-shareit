package ru.practicum.shareit.request;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Accessors(chain = true)
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
