package com.sparta.ourportfolio.common.jwt;

import com.sparta.ourportfolio.JacocoGenerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@JacocoGenerated
@Data
@NoArgsConstructor
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;

    public JwtTokenDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
