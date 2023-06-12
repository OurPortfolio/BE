package com.sparta.ourportfolio.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.enums.UserRoleEnum;
import com.sparta.ourportfolio.common.jwt.JwtUtil;
import com.sparta.ourportfolio.user.dto.GoogleUserInfoDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    // https://accounts.google.com/o/oauth2/auth?client_id=694048623782-hd9kdh349dqu8ja6aol2ie9ng6ls9o3f.apps.googleusercontent.com&redirect_uri=http://polpro.s3-website.ap-northeast-2.amazonaws.com&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email

    public ResponseDto<String> googleLogin(String code, String scope, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code, scope);

        // 2. 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
        GoogleUserInfoDto googleUserInfoDto = getGoogleUserInfo(accessToken);

        // 3. 필요시에 회원가입
        User googleUser = registerGoogleUserIfNeeded(googleUserInfoDto);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(googleUser.getEmail(), "Access", googleUser.getId());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, createToken);

        return ResponseDto.setSuccess(HttpStatus.OK, "구글 로그인 성공!");
    }

    // 1. "인가 코드"로 "액세스 토큰 & 리프레시 토큰" 요청
    private String getToken(String code, String scope) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", googleClientId);
        body.add("client_secret", googleClientSecret);
        body.add("redirect_uri", "http://polpro.s3-website.ap-northeast-2.amazonaws.com");
        body.add("code", code);
        body.add("scope", scope);
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> tokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                tokenRequest,
                String.class
        );
        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();

    }

    // 2. 토큰으로 구글 API 호출 : "액세스 토큰"으로 "구글 사용자 정보" 가져오기
    private GoogleUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.POST,
                googleUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("email").asText();
        String nickname = jsonNode.get("name") != null ? jsonNode.get("name").asText() : "google";
        String profileImage = jsonNode.get("properties")
                .get("profile_image").asText();

        log.info("구글 사용자 정보: " + id + ", " + nickname + ", " + email + ", " + profileImage);
        return new GoogleUserInfoDto(id, email, nickname, profileImage);
    }

    // 3. 필요시에 회원가입
    private User registerGoogleUserIfNeeded(GoogleUserInfoDto googleUserInfo) {
        Long googleId = googleUserInfo.getId();
        String profileImage = googleUserInfo.getProfileImage();
        User googleUser = userRepository.findByKakaoId(googleId)
                .orElse(null);
        if (googleUser == null) {
            // 구글 사용자 email 동일한 email 가진 회원이 있는지 확인
            String googleEmail = googleUserInfo.getEmail();
            User sameEmailUser = userRepository.findByEmail(googleEmail).orElse(null);
            if (sameEmailUser != null) {
                googleUser = sameEmailUser;
                // 기존 회원정보에 구글 Id, 프로필 이미지 추가
                googleUser = googleUser.kakaoUpdate(googleId, profileImage);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: google email
                String email = googleUserInfo.getEmail();

                googleUser = new User(null, null, googleId, email, googleUserInfo.getNickname(), encodedPassword, profileImage);
            }
            userRepository.save(googleUser);
        }
        return googleUser;
    }
}
