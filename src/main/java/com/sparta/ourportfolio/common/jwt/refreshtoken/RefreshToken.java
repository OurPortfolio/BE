package com.sparta.ourportfolio.common.jwt.refreshtoken;

import com.sparta.ourportfolio.JacocoGenerated;
import com.sparta.ourportfolio.common.utils.TimeStamped;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JacocoGenerated
@Getter
@Entity
@NoArgsConstructor
public class RefreshToken extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String refreshToken;

    @NotBlank
    private String email;

    public RefreshToken(String refreshToken, String email) {
        this.refreshToken = refreshToken;
        this.email = email;
    }

    public RefreshToken updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

}