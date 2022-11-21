package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UserDto {
    private Long id;
    @Email(message = "must be in email format")
    @NotNull(message = "email can not be null")
    private String email;
    private String name;
}
