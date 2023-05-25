package com.sparta.ourportfolio.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "set")
public class ResponseDto<T> {
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ResponseDto<T> setSuccess(String message, T data) {
        return ResponseDto.set(message, data);
    }

    public static <T> ResponseDto<T> setSuccess(String message) {
        return ResponseDto.set(message, null);
    }

    public static <T> ResponseDto<T> sendMessage(String message) {
        return ResponseDto.set(message, null);
    }
}
