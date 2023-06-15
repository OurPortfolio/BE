package com.sparta.ourportfolio.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.user.dto.*;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import com.sparta.ourportfolio.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }


    @DisplayName("신규회원가입")
    @Test
    void signup() throws Exception {
        // given
        SignupRequestDto signupRequestDto1 = createSignupRequestDto("test1@example.com", "Password123", "test1", null);

        // when // then
        mockMvc.perform(
                        post("/api/users/signup")
                                .content(objectMapper.writeValueAsString(signupRequestDto1))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("회원가입시 email 형식이 올바르지 않을 경우 예외가 발생한다.")
    @Test
    void signupWithWrongEmail() throws Exception {
        // given
        SignupRequestDto signupRequestDto2 = createSignupRequestDto("test2@examplecom", "Password123", "2", null);

        // when // then
        mockMvc.perform(
                        post("/api/users/signup")
                                .content(objectMapper.writeValueAsString(signupRequestDto2))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("회원가입시 email 값이 없을 경우 예외가 발생한다.")
    @Test
    void signupWithEmailIsEmpty() throws Exception {
        // given
        SignupRequestDto signupRequestDto3 = createSignupRequestDto(null, "Password123", "test3", null);

        // when // then
        mockMvc.perform(
                        post("/api/users/signup")
                                .content(objectMapper.writeValueAsString(signupRequestDto3))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("회원가입시 password 형식이 올바르지 않을 경우 예외가 발생한다.")
    @Test
    void signupWithWrongPassword() throws Exception {
        // given
        SignupRequestDto signupRequestDto4 = createSignupRequestDto("test4@example.com", "password", "test4", null);

        // when // then
        mockMvc.perform(
                        post("/api/users/signup")
                                .content(objectMapper.writeValueAsString(signupRequestDto4))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("회원가입시 password 값이 없을 경우 예외가 발생한다.")
    @Test
    void signupWithPasswordIsEmpty() throws Exception {
        // given
        SignupRequestDto signupRequestDto5 = createSignupRequestDto("test5@example.com", null, "test5", null);

        // when // then
        mockMvc.perform(
                        post("/api/users/signup")
                                .content(objectMapper.writeValueAsString(signupRequestDto5))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("회원가입시 Nickname 형식이 올바르지 않을 경우 예외가 발생한다.")
    @Test
    void signupWithWrongNickname() throws Exception {
        // given
        SignupRequestDto signupRequestDto6 = createSignupRequestDto("test6@example.com", "password123", "test67890123", null);

        // when // then
        mockMvc.perform(
                        post("/api/users/signup")
                                .content(objectMapper.writeValueAsString(signupRequestDto6))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("회원가입시 Nickname 값이 없을 경우 예외가 발생한다.")
    @Test
    void signupWithNicknameIsEmpty() throws Exception {
        // given
        SignupRequestDto signupRequestDto7 = createSignupRequestDto("test7@example.com", "password123", null, null);

        // when // then
        mockMvc.perform(
                        post("/api/users/signup")
                                .content(objectMapper.writeValueAsString(signupRequestDto7))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("로그인")
    @Test
    void login() throws Exception {
        // given
        User user1 = createUser("test8@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test8", false);
        userRepository.save(user1);

        LoginRequestDto loginRequestDto1 = createLoginRequestDto("test8@example.com", "Password123");

        // when // then
        mockMvc.perform(
                        post("/api/users/login")
                                .content(objectMapper.writeValueAsString(loginRequestDto1))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("유저 조회")
    @Test
    void getUser() throws Exception {
        // given
        User user2 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        Long userId = userRepository.save(user2).getId();


        ResponseDto<UserDto> getUserResponse = userService.getUser(user2.getId());
        // when // then
        mockMvc.perform(
                        get("/api/users/" + userId)
                                .content(objectMapper.writeValueAsString(getUserResponse))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("회원정보 수정")
    @Test
    void updateUser() throws Exception {
        // given
        User user3 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        Long userId = userRepository.save(user3).getId();

        UserDetailsImpl userDetails1 = new UserDetailsImpl(userRepository.findById(user3.getId()).get());

        UpdateUserRequestDto updateUserRequestDto1 = new UpdateUserRequestDto("test1234");
        String newCreateJson = objectMapper.writeValueAsString(updateUserRequestDto1);
        MockMultipartFile newRequestDto = new MockMultipartFile("nickname", "updateUserRequestDto1", "application/json", newCreateJson.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile image = new MockMultipartFile("profileImage", "test.jpg", "image/jpeg", "Test Image".getBytes());

        // when // then
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/api/users/" + userId)
                                .file(newRequestDto)
                                .file(image)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(user(userDetails1))
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("비밀번호 변경")
    @Test
    void updatePassword() throws Exception {
        // given
        User user4 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        Long userId = userRepository.save(user4).getId();

        UserDetailsImpl userDetails1 = new UserDetailsImpl(userRepository.findById(user4.getId()).get());

        UpdatePasswordRequestDto updatePasswordRequestDto1 = new UpdatePasswordRequestDto("Password123", "Password1234", "Password1234");

        // when // then
        mockMvc.perform(
                        put("/api/users/" + userId + "/password")
                                .content(objectMapper.writeValueAsString(updatePasswordRequestDto1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(userDetails1))
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("회원탈퇴 soft")
    @Test
    void softDelete() throws Exception {
        // given
        User user5 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        Long userId = userRepository.save(user5).getId();

        UserDetailsImpl userDetails1 = new UserDetailsImpl(userRepository.findById(user5.getId()).get());
        ResponseDto<UserDto> deleteUserResponse = userService.deleteUser(user5.getId(), user5);

        // when // then
        mockMvc.perform(
                        delete("/api/users/" + userId)
                                .with(user(userDetails1))
                                .content(objectMapper.writeValueAsString(deleteUserResponse))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("회원탈퇴 hard")
    @Test
    void hardDelete() throws Exception {
        // given
        User user6 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        User user7 = createUser("test1234@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test1234", false);
        Long userId1 = userRepository.save(user6).getId();
        Long userId2 = userRepository.save(user7).getId();

        UserDetailsImpl userDetails2 = new UserDetailsImpl(userRepository.findById(user7.getId()).get());
        ResponseDto<UserDto> hardDeleteUserResponse = userService.deleteUserHard(user7.getId(), user7);
        // when // then
        mockMvc.perform(
                        delete("/api/users/hard/" + userId2)
                                .with(user(userDetails2))
                                .content(objectMapper.writeValueAsString(hardDeleteUserResponse))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("이메일 중복체크")
    @Test
    void checkEmail() throws Exception {
        // given
        User user2 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user2);
        SignupRequestDto signupRequestDto = createSignupRequestDto("test1234@example.com", "test1234", "test1234", null);


        ResponseDto<Boolean> response = userService.checkEmail(signupRequestDto.getEmail());
        // when // then
        mockMvc.perform(
                        get("/api/users/email-check")
                                .param("email", signupRequestDto.getEmail())
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
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