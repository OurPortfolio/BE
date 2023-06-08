package com.sparta.ourportfolio.user.entity;

import com.sparta.ourportfolio.common.utils.TimeStamped;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.project.entity.ProjectImage;
import com.sparta.ourportfolio.user.dto.SignupRequestDto;
import com.sparta.ourportfolio.user.dto.UpdateUserRequestDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    @Lob
    private String profileImage;

    // soft delete
    @Column(nullable = false)
    private boolean isDeleted = false;

    private Long kakaoId;

    private Long naverId;

    private Long googleId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Portfolio> portfolioList = new ArrayList<>();

    @Builder
    public User(Long id, String email, String password, String nickname, boolean isDeleted) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.isDeleted = isDeleted;
    }

    public User(String password, SignupRequestDto signupRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.password = password;
        this.nickname = signupRequestDto.getNickname();
        this.profileImage = signupRequestDto.getProfileImage();
    }

    public void updateUser(UpdateUserRequestDto updateUserRequestDto, String imageUrl) {
        this.nickname = updateUserRequestDto.getNickname();
        this.profileImage = imageUrl;
    }

    // 소셜 회원가입
    public User(Long kakaoId, Long naverId, Long googleId, String email, String nickname, String password, String profileImage) {
        this.kakaoId = kakaoId;
        this.naverId = naverId;
        this.googleId = googleId;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
    }

    public User kakaoUpdate(Long kakaoId, String profileImage) {
        this.kakaoId = kakaoId;
        this.profileImage = profileImage;
        return this;
    }

    public User naverUpdate(Long naverId, String profileImage) {
        this.naverId = naverId;
        this.profileImage = profileImage;
        return this;
    }

    public void addPortfolio(Portfolio portfolio) {
        this.portfolioList.add(portfolio);
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void deleteUser() {
        this.isDeleted = true;
    }
}