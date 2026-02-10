package com.musclebuilder.controller.handler;

import com.musclebuilder.dto.ApiErrorResponse;
import com.musclebuilder.exception.ResourceNotFoundException;
import com.musclebuilder.exception.UnauthorizedAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_KEY = "error";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                Map.of(ERROR_KEY, ex.getMessage()),
                request.getDescription(false),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedAccess(UnauthorizedAccessException ex,
            WebRequest request) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                Map.of(ERROR_KEY, ex.getMessage()),
                request.getDescription(false),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errors,
                request.getDescription(false),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleParsingExceptions(HttpMessageNotReadableException ex,
            WebRequest request) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                Map.of(ERROR_KEY, "Malformed JSON request or invalid data type"),
                request.getDescription(false),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<ApiErrorResponse> handleOptimisticLockException(OptimisticLockingFailureException ex, WebRequest request) {

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                Map.of(ERROR_KEY, "O registro foi atualizado por outro usuário ou processo, recarregue a página e tente novamente"),
                request.getDescription(false),
                LocalDateTime.now());

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
