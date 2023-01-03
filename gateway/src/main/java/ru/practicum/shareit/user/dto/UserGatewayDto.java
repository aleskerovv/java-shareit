package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserGatewayDto {
    private Long id;
    @Email(groups = {Update.class, Create.class}, message = "must be a well-formed email address")
    @NotNull(groups = Create.class, message = "email can not be null")
    private String email;
    @NotNull(groups = Create.class, message = "name can not be null")
    private String name;
}
