package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage handleException(MethodArgumentNotValidException ex) {
        String error = Objects.requireNonNull(ex.getFieldError()).getDefaultMessage();
        log.warn(error);

        return new ErrorMessage(HttpStatus.BAD_REQUEST, error);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingRequestHeaderException.class, IncorrectEmailException.class,
            ItemIsUnavailableException.class,
            UnknownStateException.class})
    public ErrorMessage handleException(Exception ex) {
        String error = ex.getMessage();
        log.warn(error);

        return new ErrorMessage(HttpStatus.BAD_REQUEST, error);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NoAccessException.class)
    public ErrorMessage handleNoAccessException(NoAccessException ex) {
        String error = ex.getMessage();
        log.warn(error);

        return new ErrorMessage(HttpStatus.FORBIDDEN, error);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({EntityNotFoundException.class, UnsupportedOperationException.class})
    public ErrorMessage handleNotFoundException(Exception ex) {
        String error = ex.getMessage();
        log.warn(error);

        return new ErrorMessage(HttpStatus.NOT_FOUND, error);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorMessage handleIllegalArgumentException(IllegalArgumentException ex) {
        String error = ex.getMessage();
        log.warn(error);

        return new ErrorMessage(HttpStatus.CONFLICT, error);
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