package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentGatewayDto {
    private Long id;
    @Pattern(regexp = "^(?!\\s*$).+", message = "'text' must be not empty")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
