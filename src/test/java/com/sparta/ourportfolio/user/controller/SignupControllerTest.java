package com.sparta.ourportfolio.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ourportfolio.user.dto.SignupRequestDto;
import com.sparta.ourportfolio.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @DisplayName("신규회원가입")
    @Test
    void signup() throws Exception{
        // given
        SignupRequestDto signupRequestDto1 = createSignupRequestDto("test4567@example.com","Password123","test4567",null);

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
        SignupRequestDto signupRequestDto2 = createSignupRequestDto("test4567@examplecom","Password123","test4567",null);

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
        SignupRequestDto signupRequestDto3 = createSignupRequestDto(null,"Password123","test4567",null);

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
        SignupRequestDto signupRequestDto3 = createSignupRequestDto("test4567@example.com","password","test4567",null);

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

    @DisplayName("회원가입시 password 값이 없을 경우 예외가 발생한다.")
    @Test
    void signupWithPasswordIsEmpty() throws Exception {
        // given
        SignupRequestDto signupRequestDto3 = createSignupRequestDto("test4567@example.com",null,"test4567",null);

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

    @DisplayName("회원가입시 Nickname 형식이 올바르지 않을 경우 예외가 발생한다.")
    @Test
    void signupWithWrongNickname() throws Exception {
        // given
        SignupRequestDto signupRequestDto3 = createSignupRequestDto("test4567@example.com","password123","test3456789",null);

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

    @DisplayName("회원가입시 Nickname 값이 없을 경우 예외가 발생한다.")
    @Test
    void signupWithNicknameIsEmpty() throws Exception {
        // given
        SignupRequestDto signupRequestDto3 = createSignupRequestDto("test4567@example.com","password123",null,null);

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

    private SignupRequestDto createSignupRequestDto(String email, String password, String nickname, String profileImage) {
        return SignupRequestDto.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImage(profileImage)
                .build();
    }

}