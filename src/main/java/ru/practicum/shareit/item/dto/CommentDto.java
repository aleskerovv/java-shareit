package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CommentDto {
    private Long id;
    @Pattern(regexp = "^(?!\\s*$).+", message = "'text' must be not empty")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
