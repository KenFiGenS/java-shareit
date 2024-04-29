package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintValidationException(
            ConstraintViolationException e
    ) {
        log.info("Получен статус 409 Bad Request {}", e.getMessage(), e);
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(violation.getMessage())
                )
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        log.info("Получен статус 409 Bad Request {}", e.getMessage(), e);
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Violation onServiceArgumentNotValidException(IllegalArgumentException e) {
        log.info("Получен статус 400 Conflict {}", e.getMessage(), e);
        return new Violation(e.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, DataNotFound.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Violation onServiceArgumentNotValidException(RuntimeException e) {
        log.info("Получен статус 404 Not Found {}", e.getMessage(), e);
        return new Violation(e.getMessage());
    }

    @ExceptionHandler(NotFoundBookingStatusException.class)
    public ResponseEntity<Violation> bookingStatusNotFound(NotFoundBookingStatusException e) {
        log.info("Запращиваемый BookingStatus не найден. Ошибка: {}", e.getMessage(), e);
        return new ResponseEntity<>(new Violation(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UndeclaredThrowableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Violation bookingStatusNotFoundOnTransactional(UndeclaredThrowableException e) {
        log.info("Запращиваемый BookingStatus не найден. Ошибка: {}", e.getMessage(), e);
        return new Violation("Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Violation onOthersException(Exception e) {
        log.info("Получен статус 500 Internal Server Error {}", e.getMessage(), e);
        return new Violation(e.getMessage());
    }
}
