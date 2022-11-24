package ru.practicum.shareit.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorMessage {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;

    public ErrorMessage(HttpStatus httpStatus, String error) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = error;
    }
}