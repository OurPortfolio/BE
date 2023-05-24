package com.sparta.ourportfolio.common.utils;

import com.sparta.ourportfolio.common.enums.StatusEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class Message {
    private final String message;
    private final Integer statuscode;


    public Message(String messageToClient, HttpStatus httpStatus) {
        this.message = messageToClient;
        this.statuscode = httpStatus.value();
    }

    public Message(StatusEnum status) {
        this.statuscode = status.statuscode;
        this.message = status.message;
    }
}