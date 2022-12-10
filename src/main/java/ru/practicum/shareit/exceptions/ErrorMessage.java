package ru.practicum.shareit.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorMessage {
    private LocalDateTime timestamp;
    private String status;
    private String error;

    public ErrorMessage(HttpStatus httpStatus, String error) {
        this.timestamp = LocalDateTime.now();
        this.status = String.format("%d: %s", httpStatus.value(), httpStatus.getReasonPhrase());
        this.error = error;
    }
}