package com.sparta.ourportfolio.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GlobalExceptionEntity {
    private final String errorCode;
    private final String errorMessage;

    @Builder
    public GlobalExceptionEntity(HttpStatus status, String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
