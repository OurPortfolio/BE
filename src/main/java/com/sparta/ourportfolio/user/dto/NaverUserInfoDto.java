package com.sparta.ourportfolio.user.dto;

import com.sparta.ourportfolio.JacocoGenerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JacocoGenerated
@Getter
@NoArgsConstructor
public class NaverUserInfoDto {


    private Long id;
    private String nickname;
    private String email;
    private String profileImage;

    public NaverUserInfoDto(Long id, String nickname, String email, String profileImage) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
    }
}
