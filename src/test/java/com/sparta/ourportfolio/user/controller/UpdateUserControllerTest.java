package com.sparta.ourportfolio.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.user.dto.UpdateUserRequestDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import com.sparta.ourportfolio.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UpdateUserControllerTest {

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

    @DisplayName("회원정보 수정")
    @Test
    void updateUser() throws Exception {
        // given
        User user1 = createUser(1L, "test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);

        UserDetailsImpl userDetails1 = new UserDetailsImpl(userRepository.findById(1L).get());

        UpdateUserRequestDto updateUserRequestDto1 = new UpdateUserRequestDto("test1234");
        String newCreateJson = objectMapper.writeValueAsString(updateUserRequestDto1);
        MockMultipartFile newRequestDto = new MockMultipartFile("nickname", "updateUserRequestDto1", "application/json", newCreateJson.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile image = new MockMultipartFile("profileImage", "test.jpg", "image/jpeg", "Test Image".getBytes());

        // when // then
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/api/users/1")
                                .file(newRequestDto)
                                .file(image)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(user(userDetails1))
                )
                .andDo(print())
                .andExpect(status().isOk());

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