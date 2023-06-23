package com.sparta.ourportfolio.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.ourportfolio.JacocoGenerated;
import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.user.dto.*;
import com.sparta.ourportfolio.user.service.GoogleService;
import com.sparta.ourportfolio.user.service.KakaoService;
import com.sparta.ourportfolio.user.service.NaverService;
import com.sparta.ourportfolio.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final GoogleService googleService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseDto<UserDto> signup(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseDto<UserDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return userService.login(loginRequestDto, response);
    }

    // 회원 조회
    @GetMapping("/{id}")
    public ResponseDto<UserDto> getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // 회원 정보 수정
    @PatchMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<UserDto> updateUser(@PathVariable Long id,
                                           @RequestPart(name = "nickname") UpdateUserRequestDto updateUserRequestDto,
                                           @RequestPart(name = "profileImage", required = false) MultipartFile image,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return userService.updateUser(id, updateUserRequestDto, image, userDetails.user());
    }

    // 비밀번호 변경
    @PutMapping("/{id}/password")
    public ResponseDto<UserDto> updatePassword(@PathVariable Long id,
                                               @Valid @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.updatePassword(id, updatePasswordRequestDto, userDetails.user());
    }

    // 회원 탈퇴(soft)
    @DeleteMapping("/{id}")
    public ResponseDto<UserDto> deleteUser(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUser(id, userDetails.user());
    }

    // 회원 탈퇴(hard)
    @DeleteMapping("/hard/{id}")
    public ResponseDto<UserDto> deleteUserHard(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUserHard(id, userDetails.user());
    }

    //이메일 중복 검사
    @GetMapping("/email-check")
    public ResponseDto<Boolean> checkEmail(@RequestParam String email) {
        return userService.checkEmail(email);
    }

    @JacocoGenerated
    // 카카오 로그인
    @GetMapping("/kakao")
    public ResponseDto<String> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }

    @JacocoGenerated
    // 네이버 로그인
    @GetMapping("/naver")
    public ResponseDto<String> naverLogin(@RequestParam String code, @RequestParam String state, HttpServletResponse response) throws JsonProcessingException {
        return naverService.naverLogin(code, state, response);
    }

    @JacocoGenerated
    // 구글 로그인
    @GetMapping("/google")
    public ResponseDto<String> googleLogin(@RequestParam String code, @RequestParam String scope, HttpServletResponse response) throws JsonProcessingException {
        return googleService.googleLogin(code, scope, response);
    }

    @JacocoGenerated
    //Refresh 토큰으로 Access 토큰 재발급
    @GetMapping("/reissue")
    public ResponseDto<UserDto> reissueToken(@RequestParam(value = "REFRESH_TOKEN", required = false) String refreshToken, HttpServletResponse response) {
        return userService.reissueToken(refreshToken, response);
    }
}