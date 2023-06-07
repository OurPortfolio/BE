package com.sparta.ourportfolio.user.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.user.dto.LoginRequestDto;
import com.sparta.ourportfolio.user.dto.UserDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LoginServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse response;

    @DisplayName("로그인")
    @Test
    void login() {
        // given
        User user1 = createUser("test4@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4", false);
        userRepository.save(user1);

        LoginRequestDto loginRequestDto1 = createLoginRequestDto("test4@example.com", "Password123");

        // 로그인 요청 수행
        ResponseDto<UserDto> loginResponse = userService.login(loginRequestDto1, response);

        // 응답 검증
        assertThat(loginResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "로그인 성공!");
    }

    @DisplayName("해당 email로 가입한 회원이 존재하지 않을 시 예외를 반환한다.")
    @Test
    void loginNotFoundUser() {
        // given
        User user2 = createUser("test5@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test5", false);
        userRepository.save(user2);

        LoginRequestDto loginRequestDto2 = createLoginRequestDto("test45678@example.com", "Password123");

        // when // then
        assertThatThrownBy(() -> userService.login(loginRequestDto2, response))
                .isInstanceOf(GlobalException.class)
                .hasMessage("회원이 존재하지 않습니다.");
    }

    @DisplayName("이미 탈퇴한 회원으로 로그인 시 예외를 반환한다.")
    @Test
    void loginAlreadyDeleteUser() {
        // given
        User user3 = createUser("test6@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test6", true);
        userRepository.save(user3);

        LoginRequestDto loginRequestDto3 = createLoginRequestDto("test6@example.com", "Password123");

        // when // then
        assertThatThrownBy(() -> userService.login(loginRequestDto3, response))
                .isInstanceOf(GlobalException.class)
                .hasMessage("회원 탈퇴가 된 상태입니다.");
    }

    @DisplayName("잘못된 비밀번호로 로그인 시 예외를 반환한다.")
    @Test
    void loginWithWrongPassword() {
        // given
        User user4 = createUser("test7@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test7", false);
        userRepository.save(user4);

        LoginRequestDto loginRequestDto4 = createLoginRequestDto("test7@example.com", "Password");

        // when // then
        assertThatThrownBy(() -> userService.login(loginRequestDto4, response))
                .isInstanceOf(GlobalException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
    }

    private User createUser(String email, String password, String nickname, boolean isDeleted) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .isDeleted(isDeleted)
                .build();
    }

    private LoginRequestDto createLoginRequestDto(String email, String password) {
        return LoginRequestDto.builder()
                .email(email)
                .password(password)
                .build();
    }

}