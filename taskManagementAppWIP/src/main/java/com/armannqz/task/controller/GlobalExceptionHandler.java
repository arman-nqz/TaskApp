package com.armannqz.task.controller;

import com.armannqz.task.domain.dto.ErrorDto;
import com.armannqz.task.exception.TaskNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

/**
 * Global exception handler for the application.
 *
 * <p>Annotated with {@link ControllerAdvice}, this class acts as a centralized
 * interceptor for exceptions thrown across all controller classes. It catches
 * errors (such as client-side bad requests) and converts them into standardized,
 * user-friendly JSON responses instead of exposing raw stack traces or default error pages.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles client-side validation failures (HTTP 400 Bad Request).
     *
     * <p>This method is triggered whenever incoming request data fails Jakarta/Hibernate
     * validation constraints (e.g., {@code @Valid}, {@code @NotBlank}, {@code @Length}, etc.)
     * defined on request DTOs like {@code CreateTaskRequestDto}.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {

        // 1. Extract the first validation failure message from the exception:
        //    - getBindingResult(): contains the results and details of the validation failure.
        //    - getFieldErrors(): retrieves the list of individual field validation errors.
        //    - stream().findFirst(): picks the first failed field constraint.
        //    - map(...): extracts the human-readable default error message defined on the DTO.
        //    - orElse(...): provides a fallback message if no specific message is found.
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation Failed.");

        ErrorDto errorDto = new ErrorDto(errorMessage);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorDto> handleTaskNotFoundException(TaskNotFoundException ex) {
        UUID taskNotFoundId = ex.getId();
        String errorMessage = String.format("Task with ID '%s' not found", taskNotFoundId);
        ErrorDto errorDto = new ErrorDto(errorMessage);
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }
}
