package com.notemate.app.common.exception;

import com.notemate.app.common.dto.response.CustomApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomApiResponse<String>> handleGeneralException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomApiResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation error");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomApiResponse.error(errorMessage));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CustomApiResponse<String>> handleUnauthorized(UnauthorizedException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomApiResponse<String>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<CustomApiResponse<String>> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomApiResponse<String>> handleBadRequest(BadRequestException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<CustomApiResponse<String>> handleTooManyRequests(TooManyRequestsException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<CustomApiResponse<String>> handleInternalServerError(InternalServerErrorException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomApiResponse.error(ex.getMessage()));
    }
}
