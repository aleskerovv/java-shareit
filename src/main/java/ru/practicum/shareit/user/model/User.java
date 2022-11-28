package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Accessors(chain = true)
public class User {
    private Long id;
    private String email;
    private String name;
}
