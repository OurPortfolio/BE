package com.sparta.ourportfolio.user.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.user.dto.SignupRequestDto;
import com.sparta.ourportfolio.user.dto.UserDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class SignupServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @DisplayName("신규회원가입")
    @Test
    void signup() {
        // given
        SignupRequestDto signupRequestDto1 = createSignupRequestDto("test4567@example.com", "Password123", "test4567", null);

        // when (회원가입 메서드 호출)
        ResponseDto<UserDto> response = userService.signup(signupRequestDto1);

        // then (결과 검증)
        assertThat(response)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "회원가입 성공!");

        // 저장소에 사용자가 저장되었는지 확인
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1)
                .extracting("email", "nickname", "profileImage")
                .containsExactlyInAnyOrder(
                        tuple("test4567@example.com", "test4567", null)
                );

    }

//    @DisplayName("중복된 Email로 회원가입")
//    @Test
//    void signupWitheDuplicateEmail() {
//        // given
//        SignupRequestDto signupRequestDto2 = createSignupRequestDto("test4567@example.com", "Password123", "test4", null);
//
//        // when // then
//        assertThatThrownBy(() -> userService.signup(signupRequestDto2))
//                .isInstanceOf(GlobalException.class)
//                .hasMessage("중복된 아이디가 이미 존재합니다.");
//
//    }

    @DisplayName("중복된 Nickname으로 회원가입")
    @Test
    void signupWitheDuplicateNickname() {
        // given
        SignupRequestDto signupRequestDto3 = createSignupRequestDto("test4@example.com", "Password123", "test4567", null);
        // when // then
        assertThatThrownBy(() -> userService.signup(signupRequestDto3))
                .isInstanceOf(GlobalException.class)
                .hasMessage("중복된 닉네임이 이미 존재합니다.");

    }

    private SignupRequestDto createSignupRequestDto(String email, String password, String nickname, String profileImage) {
        return SignupRequestDto.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }

}