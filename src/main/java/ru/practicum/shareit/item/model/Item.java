package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Accessors(chain = true)
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean isAvailable;
    private UserDto owner;
    private ItemRequest request;
}
