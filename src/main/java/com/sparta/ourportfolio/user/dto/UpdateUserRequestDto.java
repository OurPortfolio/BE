package com.sparta.ourportfolio.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserRequestDto {
    private String nickname;
    private String profileImage;

    public UpdateUserRequestDto(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}