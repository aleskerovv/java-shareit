package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage handleException(MethodArgumentNotValidException ex) {
        String error = Objects.requireNonNull(ex.getFieldError()).getDefaultMessage();
        log.warn(error);

        return new ErrorMessage(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                error);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ErrorMessage handleRequestHeaderException(MissingRequestHeaderException ex) {
        String error = ex.getMessage();
        log.warn(error);

        return new ErrorMessage(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                error);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NoAccessException.class)
    public ErrorMessage handleNoAccessException(NoAccessException ex) {
        String error = ex.getMessage();
        log.warn(error);

        return new ErrorMessage(LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                error);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorMessage handleNotFoundException(NotFoundException ex) {
        String error = ex.getMessage();
        log.warn(error);

        return new ErrorMessage(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                error);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IncorrectEmailException.class)
    public ErrorMessage handleIncorrectEmailException(IncorrectEmailException ex) {
        String error = ex.getMessage();
        log.warn(error);

        return new ErrorMessage(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                error);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorMessage handleIllegalArgumentException(IllegalArgumentException ex) {
        String error = ex.getMessage();
        log.warn(error);

        return new ErrorMessage(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                error);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UnsupportedOperationException.class)
    protected ErrorMessage handleUnsupportedOperationException(
            UnsupportedOperationException ex) {
        String error = ex.getMessage();
        log.error(error);

        return new ErrorMessage(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                error);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleThrowable(final Throwable e) {
        String error = "Internal server error";
        log.error(error);
        e.printStackTrace();

        return error;
    }
}