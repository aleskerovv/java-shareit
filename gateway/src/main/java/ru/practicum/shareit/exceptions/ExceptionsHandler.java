package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ExceptionsHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorMessage handleException(MethodArgumentNotValidException ex) {
        String error = Objects.requireNonNull(ex.getFieldError()).getDefaultMessage();
        log.warn(error);

        return new ErrorMessage(HttpStatus.BAD_REQUEST, error);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class,
            ConstraintViolationException.class})
    public ErrorMessage handleIllegalArgumentException(Exception ex) {
        String error = ex.getMessage();
        log.warn(error);

        return new ErrorMessage(HttpStatus.BAD_REQUEST, error);
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
