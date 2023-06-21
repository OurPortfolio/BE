package com.sparta.ourportfolio.user.email.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("이메일 인증")
    @Test
    void sendSimpleMessage() throws Exception {
        // given
        String receiverEmail = "test1234@example.com";

        // when (회원가입 메서드 호출)
        ResponseDto<String> response = emailService.sendSimpleMessage(receiverEmail);

        // then (결과 검증)
        assertThat(response)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "메일 전송에 성공하였습니다.");

    }

    @DisplayName("이메일 인증 시 중복된 이메일이 있으면 예외가 발생한다.")
    @Test
    void sendSimpleMessageWithDuplicated() throws Exception {
        // given
        User user = createUser("test1234@example.com", "$2a$10$bNzdSYiE93xquo8JzDEexuobQPahVu1RYSaGjVP5/nqy5BMJSO3ZO", "test1234", false);
        userRepository.save(user);

        String receiverEmail = "test1234@example.com";

        // when // then
        assertThatThrownBy(() -> emailService.sendSimpleMessage(receiverEmail))
                .isInstanceOf(GlobalException.class)
                .hasMessage("중복된 이메일이 이미 존재합니다.");
    }

    private User createUser(String email, String password, String nickname, boolean isDeleted) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .isDeleted(isDeleted)
                .build();
    }

}