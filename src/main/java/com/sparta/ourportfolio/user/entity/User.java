package com.sparta.ourportfolio.user.entity;

import com.sparta.ourportfolio.common.utils.TimeStamped;
import com.sparta.ourportfolio.user.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "users")
public class User extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = true)
    private String profileImage;

    public User(String password, SignupRequestDto signupRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.password = password;
        this.nickname = signupRequestDto.getNickname();
        this.profileImage = signupRequestDto.getProfileImage();
    }
}
