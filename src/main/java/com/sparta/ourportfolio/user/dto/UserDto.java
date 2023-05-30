package com.sparta.ourportfolio.user.dto;

import lombok.Getter;

@Getter
public class UserDto {
    private final Long id;

    private final String email;

    private final String nickname;

    private final String profileImage;

    private final Long kakaoId;

    public UserDto(Long id, String email, String nickname, String profileImage, Long kakaoId) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.kakaoId = kakaoId;
    }

}
