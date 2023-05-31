package com.sparta.ourportfolio.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import static com.sparta.ourportfolio.common.exception.ExceptionEnum.REQUEST_DATA_BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<GlobalExceptionEntity> exceptionHandler(GlobalException e) {
        e.printStackTrace();
        return ResponseEntity
                .status(e.getError().getStatus())
                .body(GlobalExceptionEntity.builder()
                        .errorCode(e.getError().getCode())
                        .errorMessage(e.getError().getMessage())
                        .build());
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<GlobalExceptionEntity> MissingServletRequestPartExceptionHandler(MissingServletRequestPartException e) {
        return GlobalExceptionEntity.toResponseEntity(REQUEST_DATA_BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<GlobalExceptionEntity> MissingServletRequestParameterExceptionHandler() {
        return GlobalExceptionEntity.toResponseEntity(REQUEST_DATA_BAD_REQUEST);
    }
}