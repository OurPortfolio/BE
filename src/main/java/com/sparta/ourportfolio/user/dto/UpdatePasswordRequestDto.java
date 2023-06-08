package com.sparta.ourportfolio.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePasswordRequestDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String checkNewPassword;

    public UpdatePasswordRequestDto(String oldPassword, String newPassword, String checkNewPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.checkNewPassword = checkNewPassword;
    }
}
