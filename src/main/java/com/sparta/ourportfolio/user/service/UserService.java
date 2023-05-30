package com.sparta.ourportfolio.user.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.common.jwt.JwtTokenDto;
import com.sparta.ourportfolio.common.jwt.JwtUtil;
import com.sparta.ourportfolio.common.jwt.refreshToken.RefreshToken;
import com.sparta.ourportfolio.common.jwt.refreshToken.RefreshTokenRepository;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.user.dto.*;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final S3Service s3Service;

    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    //회원가입
    public ResponseDto<HttpStatus> signup(SignupRequestDto signupRequestDto) {
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        Optional<User> findUserByEmail = userRepository.findByEmail(signupRequestDto.getEmail());
        if (findUserByEmail.isPresent()) {
            throw new IllegalArgumentException("이미 해당 이메일이 존재합니다.");
        }

        Optional<User> findNicknameByEmail = userRepository.findByNickname(signupRequestDto.getNickname());
        if (findNicknameByEmail.isPresent()) {
            throw new IllegalArgumentException("이미 해당 닉네임이 존재합니다.");
        }

        User user = new User(password, signupRequestDto);
        userRepository.save(user);
        return ResponseDto.setSuccess(HttpStatus.OK, "회원가입 성공!");
    }

    //로그인
    public ResponseDto<String> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀립니다.");
        }

        JwtTokenDto tokenDto = jwtUtil.createAllToken(email, user.getId());
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(user.getEmail());
        if (refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
        } else {
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), user.getEmail());
            refreshTokenRepository.save(newToken);
        }
        setHeader(response, tokenDto);
        return ResponseDto.setSuccess(HttpStatus.OK, "로그인 성공!");
    }

    // 회원 조회
    public ResponseDto<UserDto> getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getNickname(), user.getProfileImage(), user.getKakaoId());
        return ResponseDto.setSuccess(HttpStatus.OK, "회원 조회 성공!", userDto);
    }

    // 비밀번호 변경
    public ResponseDto<String> updatePassword(Long id, UpdatePasswordRequestDto updatePasswordRequestDto, User user) {
        userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자가 없습니다."));

        if(!passwordEncoder.matches(updatePasswordRequestDto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if(!updatePasswordRequestDto.getNewPassword().equals(updatePasswordRequestDto.getCheckNewPassword())) {
            throw new IllegalArgumentException("새로운 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 형식이 일치하는지 체크
        Pattern passPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$");
        Matcher matcher = passPattern.matcher(updatePasswordRequestDto.getNewPassword());
        if (!matcher.find()) {
            throw new IllegalArgumentException("비밀번호를 형식에 맞춰 올바르게 입력바랍니다.");
        };

        user.updatePassword(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()));
        userRepository.save(user);
        return ResponseDto.setSuccess(HttpStatus.OK, "비밀번호 변경 성공!");
    }

    // 회원 탈퇴
    public ResponseDto<HttpStatus> deleteUser(Long id, User user) {
        userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 사용자가 없습니다."));

        user.deleteUser(); // Soft delete 수행
        userRepository.save(user);

        return ResponseDto.setSuccess(HttpStatus.OK, "회원 탈퇴 성공!");
    }

    private void setHeader(HttpServletResponse response, JwtTokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }
}

