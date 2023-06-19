package com.sparta.ourportfolio.user.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserConstructor() {
        // given
        Long kakaoId = 12345L;
        Long naverId = 67890L;
        Long googleId = 54321L;
        String email = "test@example.com";
        String nickname = "testuser";
        String password = "test1234@";
        String profileImage = "profile.jpg";

        // when
        User user = new User(kakaoId, naverId, googleId, email, nickname, password, profileImage);

        // then
        assertEquals(kakaoId, user.getKakaoId());
        assertEquals(naverId, user.getNaverId());
        assertEquals(googleId, user.getGoogleId());
        assertEquals(email, user.getEmail());
        assertEquals(nickname, user.getNickname());
        assertEquals(password, user.getPassword());
        assertEquals(profileImage, user.getProfileImage());
    }

}