package ru.practicum.shareit.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
}