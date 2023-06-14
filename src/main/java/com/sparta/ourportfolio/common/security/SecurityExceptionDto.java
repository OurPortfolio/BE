package com.sparta.ourportfolio.common.security;

import com.sparta.ourportfolio.JacocoGenerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JacocoGenerated
@Getter
@NoArgsConstructor
public class SecurityExceptionDto {

    private int statusCode;
    private String message;

    public SecurityExceptionDto(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}

