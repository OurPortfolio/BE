package com.sparta.ourportfolio.user.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.jwt.JwtTokenDto;
import com.sparta.ourportfolio.common.jwt.JwtUtil;
import com.sparta.ourportfolio.common.jwt.refreshToken.RefreshToken;
import com.sparta.ourportfolio.common.jwt.refreshToken.RefreshTokenRepository;
import com.sparta.ourportfolio.user.dto.LoginRequestDto;
import com.sparta.ourportfolio.user.dto.SignupRequestDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

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
        if(!passwordEncoder.matches(password, user.getPassword())){
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



    private void setHeader(HttpServletResponse response, JwtTokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }
}

