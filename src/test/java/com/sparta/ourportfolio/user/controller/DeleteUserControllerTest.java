package com.sparta.ourportfolio.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import com.sparta.ourportfolio.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeleteUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("회원탈퇴 soft")
    @Test
    void softDelete() throws Exception {
        // given
        User user1 = createUser(1L, "test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);

        UserDetailsImpl userDetails1 = new UserDetailsImpl(userRepository.findById(1L).get());
        ResponseDto<HttpStatus> deleteUserResponse = userService.deleteUser(user1.getId(), user1);

        // when // then
        mockMvc.perform(
                        delete("/api/users/1")
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails1))
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
        User user2 = createUser(2L, "test1234@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test1234", false);
        userRepository.save(user2);

        UserDetailsImpl userDetails2 = new UserDetailsImpl(userRepository.findById(1L).get());
        ResponseDto<HttpStatus> hardDeleteUserResponse = userService.deleteUserHard(user2.getId(), user2);
        // when // then
        mockMvc.perform(
                        delete("/api/users/hard/2")
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails2))
                                .content(objectMapper.writeValueAsString(hardDeleteUserResponse))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
        ;
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