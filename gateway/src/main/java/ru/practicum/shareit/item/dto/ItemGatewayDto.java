package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemGatewayDto {
    private Long id;
    @NotBlank(groups = Create.class, message = "'name' can not be blank")
    private String name;
    @NotNull(groups = Create.class, message = "'description' can not be null")
    private String description;
    @NotNull(groups = Create.class, message = "'available' can not be null")
    private Boolean available;
    private Long requestId;
}
