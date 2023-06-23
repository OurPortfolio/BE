package com.sparta.ourportfolio.common.exception;

import com.sparta.ourportfolio.JacocoGenerated;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@JacocoGenerated
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalExceptionEntity> exceptionHandler(GlobalException e) {
        return ResponseEntity
                .status(e.getError().getStatus())
                .body(GlobalExceptionEntity.builder()
                        .errorCode(e.getError().getCode())
                        .errorMessage(e.getError().getMessage())
                        .build());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<GlobalExceptionEntity> missingServletRequestPartExceptionHandler(MissingServletRequestPartException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(GlobalExceptionEntity.builder()
                        .errorCode(e.getStatusCode().toString())
                        .errorMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<GlobalExceptionEntity> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(GlobalExceptionEntity.builder()
                        .errorCode(e.getStatusCode().toString())
                        .errorMessage(e.getMessage())
                        .build());
    }
}
