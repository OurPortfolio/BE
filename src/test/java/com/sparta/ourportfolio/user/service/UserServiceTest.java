package com.sparta.ourportfolio.user.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.user.dto.*;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse response;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("신규회원가입")
    @Test
    void signup() {
        // given
        SignupRequestDto signupRequestDto = createSignupRequestDto("test1234@example.com", "test1234", "test1234", null);

        // when (회원가입 메서드 호출)
        ResponseDto<UserDto> response = userService.signup(signupRequestDto);

        // then (결과 검증)
        assertThat(response)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "회원가입 성공!");

        // 저장소에 사용자가 저장되었는지 확인
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1)
                .extracting("email", "nickname", "profileImage")
                .containsExactlyInAnyOrder(
                        tuple("test1234@example.com", "test1234", null)
                );

    }

    @DisplayName("중복된 Nickname으로 회원가입")
    @Test
    void signupWitheDuplicateNickname() {
        // given
        User user = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test1234", false);
        userRepository.save(user);

        SignupRequestDto signupRequestDto = createSignupRequestDto("test1234@example.com", "test1234", "test1234", null);

        // when // then
        assertThatThrownBy(() -> userService.signup(signupRequestDto))
                .isInstanceOf(GlobalException.class)
                .hasMessage("중복된 닉네임이 이미 존재합니다.");

    }

    @DisplayName("로그인")
    @Test
    void login() {
        // given
        User user = createUser("test4@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4", false);
        userRepository.save(user);

        LoginRequestDto loginRequestDto = createLoginRequestDto("test4@example.com", "Password123");

        // 로그인 요청 수행
        ResponseDto<UserDto> loginResponse = userService.login(loginRequestDto, response);

        // 응답 검증
        assertThat(loginResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "로그인 성공!");
    }

    @DisplayName("해당 email로 가입한 회원이 존재하지 않을 시 예외를 반환한다.")
    @Test
    void loginNotFoundUser() {
        // given
        User user = createUser("test5@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test5", false);
        userRepository.save(user);

        LoginRequestDto loginRequestDto = createLoginRequestDto("test45678@example.com", "Password123");

        // when // then
        assertThatThrownBy(() -> userService.login(loginRequestDto, response))
                .isInstanceOf(GlobalException.class)
                .hasMessage("회원이 존재하지 않습니다.");
    }

    @DisplayName("이미 탈퇴한 회원으로 로그인 시 예외를 반환한다.")
    @Test
    void loginAlreadyDeleteUser() {
        // given
        User user = createUser("test6@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test6", true);
        userRepository.save(user);

        LoginRequestDto loginRequestDto = createLoginRequestDto("test6@example.com", "Password123");

        // when // then
        assertThatThrownBy(() -> userService.login(loginRequestDto, response))
                .isInstanceOf(GlobalException.class)
                .hasMessage("회원 탈퇴가 된 상태입니다.");
    }

    @DisplayName("잘못된 비밀번호로 로그인 시 예외를 반환한다.")
    @Test
    void loginWithWrongPassword() {
        // given
        User user = createUser("test7@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test7", false);
        userRepository.save(user);

        LoginRequestDto loginRequestDto = createLoginRequestDto("test7@example.com", "Password7891");

        // when // then
        assertThatThrownBy(() -> userService.login(loginRequestDto, response))
                .isInstanceOf(GlobalException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("유저 조회")
    @Test
    void getUser() {
        // given
        User user = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test1234", false);
        userRepository.save(user);

        // when
        ResponseDto<UserDto> userResponse = userService.getUser(user.getId());

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

    @DisplayName("회원정보 수정")
    @Test
    void updateUser() throws IOException {
        // given
        User user = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user);
        UpdateUserRequestDto updateUserRequestDto1 = new UpdateUserRequestDto("test1234");

        String imageUrl;
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());
        imageUrl = s3Service.uploadFile(image);

        user.updateUser(updateUserRequestDto1, imageUrl);
        userRepository.save(user);

        // when
        ResponseDto<UserDto> userResponse = userService.updateUser(user.getId(), updateUserRequestDto1, image, user);

        // then
        assertThat(userResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "회원 정보 수정 성공!");

    }

    @DisplayName("image 파일이 없는 상태로 회원정보 수정")
    @Test
    void updateUserWithoutImage() throws IOException {
        // given
        User user = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user);
        UpdateUserRequestDto updateUserRequestDto1 = new UpdateUserRequestDto("test1234");

        String imageUrl;
        MockMultipartFile image = null;
        imageUrl = user.getProfileImage();

        user.updateUser(updateUserRequestDto1, imageUrl);
        userRepository.save(user);

        // when
        ResponseDto<UserDto> userResponse = userService.updateUser(user.getId(), updateUserRequestDto1, image, user);

        // then
        assertThat(userResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "회원 정보 수정 성공!");

    }

    @DisplayName("로그인한 유저의 id 값과 수정하려는 id 값이 다를 시 예외를 반환한다")
    @Test
    void updateUserUnauthorized() {
        // given
        User user = createUser("test5@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test5", false);
        userRepository.save(user);
        UpdateUserRequestDto updateUserRequestDto2 = new UpdateUserRequestDto("test1234");

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());

        // when // then
        assertThatThrownBy(() -> userService.updateUser(2L, updateUserRequestDto2, image, user))
                .isInstanceOf(GlobalException.class)
                .hasMessage("권한이 없습니다.");
    }

    @DisplayName("변경하려는 닉네임이 기존 닉네임과 일치하지 않고, 이미 존재하는 닉네임일때 예외를 반환한다.")
    @Test
    void updateUserWithDuplicatedNickname() {
        // given
        User user1 = createUser("test7890@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test7890", false);
        User user2 = createUser("test0000@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test0000", false);
        userRepository.save(user1);
        userRepository.save(user2);
        UpdateUserRequestDto updateUserRequestDto3 = new UpdateUserRequestDto("test0000");

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "Test Image".getBytes());

        // when // then
        assertThatThrownBy(() -> userService.updateUser(user1.getId(), updateUserRequestDto3, image, user1))
                .isInstanceOf(GlobalException.class)
                .hasMessage("중복된 닉네임이 이미 존재합니다.");
    }

    @DisplayName("비밀번호 변경")
    @Test
    void updatePassword() {
        // given
        User user = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user);
        UpdatePasswordRequestDto updatePasswordRequestDto1 = new UpdatePasswordRequestDto("Password123", "Password1234", "Password1234");

        // when
        ResponseDto<UserDto> userResponse = userService.updatePassword(user.getId(), updatePasswordRequestDto1, user);

        // then
        assertThat(userResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "비밀번호 변경 성공!");
    }

    @DisplayName("로그인한 유저의 id 값과 수정하려는 id 값이 다를 시 예외를 반환한다")
    @Test
    void updatePasswordUnauthorized() {
        // given
        User user1 = createUser("test1234@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test1234", false);
        User user2 = createUser("test5@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test5", false);
        userRepository.save(user1);
        userRepository.save(user2);
        UpdatePasswordRequestDto updatePasswordRequestDto2 = new UpdatePasswordRequestDto("Password123", "Password1234", "Password1234");

        // when // then
        assertThatThrownBy(() -> userService.updatePassword(user1.getId(), updatePasswordRequestDto2, user2))
                .isInstanceOf(GlobalException.class)
                .hasMessage("권한이 없습니다.");
    }

    @DisplayName("입력한 비밀번호와 기존 비밀번호가 일치하지 않을 시 예외를 반환한다")
    @Test
    void updatePasswordWithPresentPassword() {
        // given
        User user3 = createUser("test5678@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test5678", false);
        userRepository.save(user3);
        UpdatePasswordRequestDto updatePasswordRequestDto3 = new UpdatePasswordRequestDto("Password12", "Password1234", "Password1234");

        // when // then
        assertThatThrownBy(() -> userService.updatePassword(user3.getId(), updatePasswordRequestDto3, user3))
                .isInstanceOf(GlobalException.class)
                .hasMessage("입력한 비밀번호와 기존 비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("입력한 새로운 비밀번호와 확인 비밀번호가 일치하지 않을 시 예외를 반환한다")
    @Test
    void updatePasswordWithCoincidePassword() {
        // given
        User user4 = createUser("test2345@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test2345", false);
        userRepository.save(user4);
        UpdatePasswordRequestDto updatePasswordRequestDto4 = new UpdatePasswordRequestDto("Password123", "Password1234", "Password123");

        // when // then
        assertThatThrownBy(() -> userService.updatePassword(user4.getId(), updatePasswordRequestDto4, user4))
                .isInstanceOf(GlobalException.class)
                .hasMessage("새로운 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
    }

    @DisplayName("회원탈퇴 soft")
    @Test
    void softDelete() {
        // given
        User user1 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);

        // when
        ResponseDto<UserDto> deleteUserResponse = userService.deleteUser(user1.getId(), user1);

        // then
        assertThat(deleteUserResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "회원 탈퇴 성공!");
    }

    @DisplayName("회원탈퇴 hard")
    @Test
    void hardDelete() {
        // given
        User user2 = createUser("test1234@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test1234", false);
        userRepository.save(user2);

        // when
        ResponseDto<UserDto> hardDeleteUserResponse = userService.deleteUserHard(user2.getId(), user2);

        // then
        assertThat(hardDeleteUserResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "영구 삭제");
    }

    @DisplayName("회원탈퇴 soft를 할 때 잘못된 id를 입력시 예외를 반환한다")
    @Test
    void softDeleteWithWrongId() {
        // given
        User user3 = createUser("test3456@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test3456", false);
        userRepository.save(user3);
        // when // then
        assertThatThrownBy(() -> userService.deleteUser(100L, user3))
                .isInstanceOf(GlobalException.class)
                .hasMessage("회원이 존재하지 않습니다.");
    }

    private SignupRequestDto createSignupRequestDto(String email, String password, String nickname, String profileImage) {
        return SignupRequestDto.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
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