package com.sparta.ourportfolio.common.exception;

import com.sparta.ourportfolio.common.utils.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Message> handleIllegalArgumentException(IllegalArgumentException e) {
        Message message = new Message(e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}