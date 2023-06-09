package com.sparta.ourportfolio.user.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.user.dto.UpdatePasswordRequestDto;
import com.sparta.ourportfolio.user.dto.UserDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UpdatePasswordServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("비밀번호 변경")
    @Test
    void updatePassword() {
        // given
        User user1 = createUser(1L, "test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);
        UpdatePasswordRequestDto updatePasswordRequestDto1 = new UpdatePasswordRequestDto("Password123", "Password1234", "Password1234");

        // when
        ResponseDto<UserDto> userResponse = userService.updatePassword(user1.getId(), updatePasswordRequestDto1, user1);

        // then
        assertThat(userResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "비밀번호 변경 성공!");
    }

    @DisplayName("로그인한 유저의 id 값과 수정하려는 id 값이 다를 시 예외를 반환한다")
    @Test
    void updatePasswordUnauthorized() throws IOException {
        // given
        User user2 = createUser(2L, "test5@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test5", false);
        userRepository.save(user2);
        UpdatePasswordRequestDto updatePasswordRequestDto2 = new UpdatePasswordRequestDto("Password123", "Password1234", "Password1234");

        // when // then
        assertThatThrownBy(() -> userService.updatePassword(1L, updatePasswordRequestDto2, user2))
                .isInstanceOf(GlobalException.class)
                .hasMessage("권한이 없습니다.");
    }

    @DisplayName("입력한 비밀번호와 기존 비밀번호가 일치하지 않을 시 예외를 반환한다")
    @Test
    void updatePasswordWithPresentPassword() throws IOException {
        // given
        User user3 = createUser(3L, "test5@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test5", false);
        userRepository.save(user3);
        UpdatePasswordRequestDto updatePasswordRequestDto3 = new UpdatePasswordRequestDto("Password12", "Password1234", "Password1234");

        // when // then
        assertThatThrownBy(() -> userService.updatePassword(3L, updatePasswordRequestDto3, user3))
                .isInstanceOf(GlobalException.class)
                .hasMessage("입력한 비밀번호와 기존 비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("입력한 새로운 비밀번호와 확인 비밀번호가 일치하지 않을 시 예외를 반환한다")
    @Test
    void updatePasswordWithCoincidePassword() throws IOException {
        // given
        User user4 = createUser(4L, "test5@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test5", false);
        userRepository.save(user4);
        UpdatePasswordRequestDto updatePasswordRequestDto4 = new UpdatePasswordRequestDto("Password123", "Password1234", "Password123");

        // when // then
        assertThatThrownBy(() -> userService.updatePassword(4L, updatePasswordRequestDto4, user4))
                .isInstanceOf(GlobalException.class)
                .hasMessage("새로운 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
    }

    private User createUser(Long id, String email, String password, String nickname, boolean isDeleted) {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .nickname(nickname)
                .isDeleted(isDeleted)
                .build();
    }

}