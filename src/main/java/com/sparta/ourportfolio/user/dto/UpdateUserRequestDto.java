package com.sparta.ourportfolio.user.dto;

import lombok.Getter;

@Getter
public class UpdateUserRequestDto {
    private String password;
    private String nickname;
    private String profileImage;
}
