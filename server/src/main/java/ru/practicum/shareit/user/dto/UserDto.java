package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UserDto {
    private Long id;
    @Email(groups = {Update.class, Create.class}, message = "must be a well-formed email address")
    @NotNull(groups = Create.class, message = "email can not be null")
    private String email;
    @NotNull(groups = Create.class, message = "name can not be null")
    private String name;
}
