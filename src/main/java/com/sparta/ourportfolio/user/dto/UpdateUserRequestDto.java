package com.sparta.ourportfolio.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserRequestDto {
    private String nickname;

    public UpdateUserRequestDto(String nickname) {
        this.nickname = nickname;
    }
}