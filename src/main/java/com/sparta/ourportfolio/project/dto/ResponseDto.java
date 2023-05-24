package com.sparta.ourportfolio.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "set")
public class ResponseDto<T> {

    private String responseMessage;
    private T data;

    public static <T> ResponseDto<T> setSuccess(String responseMessage, T data) {
        return ResponseDto.set(responseMessage, data);
    }

    public static <T> ResponseDto<T> setSuccess(String responseMessage) {
        return ResponseDto.set(responseMessage, null);
    }
}
