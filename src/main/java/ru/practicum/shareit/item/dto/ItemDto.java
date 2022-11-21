package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class ItemDto {
    private Long id;
    @NotBlank(message = "'name' can not be blank")
    private String name;
    @NotNull(message = "'description' can not be null")
    private String description;
    @NotNull(message = "'available' can not be null")
    private Boolean available;
    private UserDto owner;
    private Long requestId;
}
