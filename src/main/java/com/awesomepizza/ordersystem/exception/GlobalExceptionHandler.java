package com.awesomepizza.ordersystem.exception;

import com.awesomepizza.ordersystem.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Gestore centralizzato delle eccezioni per l'applicazione
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({PizzaException.class})
    public ResponseEntity<ErrorResponse> handlePizzaException(PizzaException ex, HttpServletRequest request) {
        log.error("PizzaException occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getPizzaErrorCode().getHttpStatus().value())
                .error(ex.getPizzaErrorCode().getHttpStatus().getReasonPhrase())
                .message(StringUtils.isNotEmpty(ex.getMessage()) ? ex.getMessage() : ex.toString())
                .path(request.getRequestURI())
                .errorCode(ex.getPizzaErrorCode().name())
                .build();

        return ResponseEntity.status(ex.getPizzaErrorCode().getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler({OrderException.class})
    public ResponseEntity<ErrorResponse> handleOrderException(OrderException ex, HttpServletRequest request) {
        log.error("OrderException occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getOrderErrorCode().getHttpStatus().value())
                .error(ex.getOrderErrorCode().getHttpStatus().getReasonPhrase())
                .message(StringUtils.isNotEmpty(ex.getMessage()) ? ex.getMessage() : ex.toString())
                .path(request.getRequestURI())
                .errorCode(ex.getOrderErrorCode().name())
                .build();

        return ResponseEntity.status(ex.getOrderErrorCode().getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error occurred: {}", ex.getMessage());

        // Raccogli tutti gli errori di validazione
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(message)
                .path(request.getRequestURI())
                .errorCode("VALIDATION_ERROR")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Si è verificato un errore interno del server")
                .path(request.getRequestURI())
                .errorCode("INTERNAL_ERROR")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
