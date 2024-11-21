package com.example.find_it.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<CustomExceptionResponseEntity> handleCustomException(CustomException e) {
        return CustomExceptionResponseEntity.toResponseEntity(e.getErrorCode());
    }
}
