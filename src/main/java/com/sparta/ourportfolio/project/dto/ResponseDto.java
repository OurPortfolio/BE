package com.sparta.ourportfolio.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "set")
public class ResponseDto<T> {

    private String responseMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ResponseDto<T> setSuccess(String responseMessage, T data) {
        return ResponseDto.set(responseMessage, data);
    }

    public static <T> ResponseDto<T> setSuccess(String responseMessage) {
        return ResponseDto.set(responseMessage, null);
    }
}
