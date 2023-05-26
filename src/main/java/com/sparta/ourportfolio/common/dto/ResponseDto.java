package com.sparta.ourportfolio.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(staticName = "set")
public class ResponseDto<T> {
    private HttpStatus statusCode;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ResponseDto<T> setSuccess(HttpStatus statusCode, String message, T data) {
        return ResponseDto.set(statusCode, message, data);
    }

    public static <T> ResponseDto<T> setSuccess(HttpStatus statusCode, String message) {
        return ResponseDto.set(statusCode, message, null);
    }
}