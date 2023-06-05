package com.sparta.ourportfolio.user.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.user.dto.UserDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class GetUserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저 조회")
    @Test
    void getUser() {
        // given
        User user1 = User.builder()
                .id(1L)
                .email("test4567@example.com")
                .password("$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK")
                .nickname("test4567")
                .isDeleted(false)
                .build();
        userRepository.save(user1);

        // when
        ResponseDto<UserDto> userResponse = userService.getUser(user1.getId());

        // then
        assertThat(userResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "회원 조회 성공!");

    }

    @DisplayName("잘못된 유저 id로 조회했을 시 예외를 반환한다.")
    @Test
    void getUserWithWrongId() {
        // when // then
        assertThatThrownBy(() -> userService.getUser(1L))
                .isInstanceOf(GlobalException.class)
                .hasMessage("회원이 존재하지 않습니다.");
    }

}