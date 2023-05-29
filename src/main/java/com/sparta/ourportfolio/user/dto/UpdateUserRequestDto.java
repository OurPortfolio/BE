package com.sparta.ourportfolio.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserRequestDto {
    private String password;
    private String nickname;
    private String profileImage;

    public UpdateUserRequestDto(String password, String nickname, String profileImage) {
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
