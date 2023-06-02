package com.sparta.ourportfolio.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class UpdateUserRequestDto {
    private Optional<String> nickname;
    private String profileImage;

    public UpdateUserRequestDto(Optional<String> nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
