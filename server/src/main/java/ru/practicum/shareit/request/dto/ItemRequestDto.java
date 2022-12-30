package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class ItemRequestDto {
    @NotNull
    private String description;
}
