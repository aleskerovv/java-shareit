package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UserDto {
    private Long id;
    @Email(message = "must be a well-formed email address")
    @NotNull(message = "email can not be null")
    private String email;
    private String name;
}
