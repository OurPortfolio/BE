package com.sparta.ourportfolio.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class GlobalExceptionEntity {
    private String errorCode;
    private String errorMessage;

    public static ResponseEntity<GlobalExceptionEntity> toResponseEntity(ExceptionEnum exceptionEnum) {
        return ResponseEntity
                .status(exceptionEnum.getStatus())
                .body(GlobalExceptionEntity.builder()
                        .errorCode(exceptionEnum.getStatus().value() + "")
                        .errorMessage(exceptionEnum.getMessage())
                        .build());
    }

    @Builder
    public GlobalExceptionEntity(HttpStatus status, String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
