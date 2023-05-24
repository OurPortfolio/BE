package com.sparta.ourportfolio.common.enums;

public enum StatusEnum {

    OK(200, "성공");

    public final Integer statuscode;
    public final String message;

    StatusEnum(Integer statuscode, String message) {
        this.statuscode = statuscode;
        this.message = message;
    }

}
