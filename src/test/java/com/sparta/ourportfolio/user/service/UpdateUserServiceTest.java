package com.sparta.ourportfolio.user.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.user.dto.UpdateUserRequestDto;
import com.sparta.ourportfolio.user.dto.UserDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UpdateUserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("회원정보 수정")
    @Test
    void updateUser() throws IOException {
        // given
        User user1 = createUser(1L, "test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);
        UpdateUserRequestDto updateUserRequestDto1 = new UpdateUserRequestDto("test1234");

        String imageUrl = null;
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());
        imageUrl = s3Service.uploadFile(image);

        user1.updateUser(updateUserRequestDto1, imageUrl);
        userRepository.save(user1);

        // when
        ResponseDto<UserDto> userResponse = userService.updateUser(user1.getId(), updateUserRequestDto1, image, user1);

        // then
        assertThat(userResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "회원 정보 수정 성공!");

    }

    @DisplayName("로그인한 유저의 id 값과 수정하려는 id 값이 다를 시 예외를 반환한다")
    @Test
    void updateUserUnauthorized() throws IOException {
        // given
        User user2 = createUser(2L, "test5@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test5", false);
        userRepository.save(user2);
        UpdateUserRequestDto updateUserRequestDto2 = new UpdateUserRequestDto("test1234");

        String imageUrl = null;
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());
        imageUrl = s3Service.uploadFile(image);

        // when // then
        assertThatThrownBy(() -> userService.updateUser(1L, updateUserRequestDto2, image, user2))
                .isInstanceOf(GlobalException.class)
                .hasMessage("권한이 없습니다.");
    }

    @DisplayName("변경하려는 닉네임이 기존 닉네임과 일치하지 않고, 이미 존재하는 닉네임일때 예외를 반환한다.")
    @Test
    void updateUserWithDuplicatedNickname() throws IOException {
        // given
        User user3 = createUser(3L, "test5@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test5", false);
        User user4 = createUser(4L, "test0000@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test0000", false);
        userRepository.save(user3);
        userRepository.save(user4);
        UpdateUserRequestDto updateUserRequestDto3 = new UpdateUserRequestDto("test0000");

        String imageUrl = null;
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());
        imageUrl = s3Service.uploadFile(image);

        // when // then
        assertThatThrownBy(() -> userService.updateUser(user3.getId(), updateUserRequestDto3, image, user3))
                .isInstanceOf(GlobalException.class)
                .hasMessage("중복된 닉네임이 이미 존재합니다.");
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