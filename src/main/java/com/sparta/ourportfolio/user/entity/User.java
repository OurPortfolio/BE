package com.sparta.ourportfolio.user.entity;

import com.sparta.ourportfolio.common.utils.TimeStamped;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.user.dto.SignupRequestDto;
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

    //카카오 회원가입
    public User(Long kakaoId, String email, String nickname, String password, String profileImage) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public void addPortfolio(Portfolio portfolio) {
        this.portfolioList.add(portfolio);
    }

    public void updateProfileImage(String imageUrl) {
        this.profileImage = imageUrl;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void deleteUser() {
        this.isDeleted = true;
    }
}