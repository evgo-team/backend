package com.project.mealplan.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.project.mealplan.common.enums.ErrorCode;
import com.project.mealplan.common.response.ApiResponse;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Xử lý AppException
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        ApiResponse<Object> response = new ApiResponse<>(
                errorCode.getCode(),
                ex.getMessage(),
                null);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    // Xử lý Validation lỗi (Bean Validation @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiResponse<Object> response = new ApiResponse<>(
                ErrorCode.INVALID_REQUEST.getCode(),
                errorMessage,
                null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Xử lý JSON parsing errors (bao gồm invalid enum values)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        log.error("JSON parsing error", ex);

        String errorMessage = "Invalid request format";

        // Check if it's an invalid enum value
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String enumName = ife.getTargetType().getSimpleName();
                String invalidValue = ife.getValue().toString();
                Object[] enumConstants = ife.getTargetType().getEnumConstants();
                String validValues = java.util.Arrays.toString(enumConstants);

                errorMessage = String.format("Invalid %s '%s'. Valid values: %s",
                        enumName, invalidValue, validValues);
            }
        }

        ApiResponse<Object> response = new ApiResponse<>(
                ErrorCode.INVALID_INGREDIENT_TYPE.getCode(),
                errorMessage,
                null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Xử lý lỗi ngoài dự kiến
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ApiResponse<Object> response = new ApiResponse<>(
                errorCode.getCode(),
                errorCode.getMessage(),
                null);
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}
