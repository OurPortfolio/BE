package com.sparta.ourportfolio.user.dto;

import com.sparta.ourportfolio.JacocoGenerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JacocoGenerated
@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;

    public KakaoUserInfoDto(Long id, String nickname, String email, String profileImage) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
    }
}
