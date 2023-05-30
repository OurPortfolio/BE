package com.sparta.ourportfolio.common.exception;

import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {
    private ExceptionEnum error;

    public GlobalException(ExceptionEnum e) {
        super(e.getMessage());
        this.error = e;
    }
}
