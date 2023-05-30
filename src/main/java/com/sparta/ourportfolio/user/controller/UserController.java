package com.sparta.ourportfolio.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.user.dto.*;
import com.sparta.ourportfolio.user.service.KakaoService;
import com.sparta.ourportfolio.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*", exposedHeaders = "Authorization")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;


    // 회원가입
    @PostMapping("/signup")
    public ResponseDto<HttpStatus> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseDto<String> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return userService.login(loginRequestDto, response);
    }

    // 회원 조회
    @GetMapping ("/{id}")
    public ResponseDto<UserDto> getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // 회원 정보 수정
    @PatchMapping (value = "/{id}",  consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<String> updateUser(@PathVariable Long id,
                                          @RequestPart(name = "nickname", required = false) UpdateUserRequestDto updateUserRequestDto,
                                          @RequestPart(name = "profileImage", required = false) Optional<MultipartFile> image,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return userService.updateUser(id, updateUserRequestDto, image, userDetails.getUser());
    }

    // 비밀번호 변경
    @PutMapping("/{id}/password")
    public ResponseDto<String> updatePassword(@PathVariable Long id,
                                              @Valid @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.updatePassword(id, updatePasswordRequestDto, userDetails.getUser());
    }

    // 회원 탈퇴(soft)
    @DeleteMapping("/{id}")
    public ResponseDto<HttpStatus> deleteUser(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUser(id, userDetails.getUser());
    }

    // 카카오 로그인
    @GetMapping("/kakao")
    public ResponseDto<String> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }
}