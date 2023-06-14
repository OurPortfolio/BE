package com.sparta.ourportfolio.user.dto;

import com.sparta.ourportfolio.JacocoGenerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JacocoGenerated
@Getter
@NoArgsConstructor
public class GoogleUserInfoDto {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;

    public GoogleUserInfoDto(Long id, String nickname, String email, String profileImage) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
    }

}
