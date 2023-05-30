package com.sparta.ourportfolio.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class UpdateUserRequestDto {
    private Optional<String> nickname;

    public UpdateUserRequestDto(Optional<String> nickname) {
        this.nickname = nickname;
    }
}
