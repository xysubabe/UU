package com.uu.exception;

import com.uu.dto.response.ApiResponse;
import com.uu.enums.ErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("Business exception: code={}, message={}", e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse<?>> handleValidationException(Exception e) {
        String message;
        if (e instanceof BindException) {
            BindException ex = (BindException) e;
            message = ex.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        }
        log.warn("Validation exception: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCodeEnum.INVALID_PARAMS.getCode(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unexpected exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCodeEnum.UNKNOWN_ERROR.getCode(), "系统错误，请稍后重试"));
    }
}