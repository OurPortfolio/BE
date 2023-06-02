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
import static com.sparta.ourportfolio.common.exception.ExceptionEnum.*;

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
            throw new GlobalException(DUPLICATED_USER_NAME);
        }

        Optional<User> findNicknameByEmail = userRepository.findByNickname(signupRequestDto.getNickname());
        if (findNicknameByEmail.isPresent()) {
            throw new GlobalException(DUPLICATED_NICK_NAME);
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
                () -> new GlobalException(NOT_FOUND_USER));

        if (user.isDeleted()) {
            throw new GlobalException(USER_IS_DELETED);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new GlobalException(BAD_REQUEST);
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
        User user = userRepository.findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER));
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getNickname(), user.getProfileImage(), user.getKakaoId());
        return ResponseDto.setSuccess(HttpStatus.OK, "회원 조회 성공!", userDto);
    }

    // 회원 정보 수정
    public ResponseDto<String> updateUser(Long id, UpdateUserRequestDto updateUserRequestDto,
                                          Optional<MultipartFile> image, User user) throws IOException {
        User userinfo = userRepository.findById(user.getId()).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER));

        if (!StringUtils.equals(user.getId(), userinfo.getId())) {
            throw new GlobalException(UNAUTHORIZED);
        }

        boolean isUpdated = false;

        // 닉네임 수정
        if (updateUserRequestDto != null && updateUserRequestDto.getNickname() != null && !updateUserRequestDto.getNickname().isEmpty()) {
            String newNickname = updateUserRequestDto.getNickname().orElse("");
            // 중복된 닉네임이 있는지 체크
            if (!newNickname.equals(userinfo.getNickname()) && userRepository.existsByNickname(newNickname)) {
                throw new GlobalException(DUPLICATED_NICK_NAME);
            }
            userinfo.updateNickname(newNickname);
            isUpdated = true;
        }

        // 클라이언트가 제공한 이미지로 업데이트
        if (image.isPresent() && !image.get().isEmpty()) {
            String imageUrl = s3Service.uploadFile(image.get());
            userinfo.updateProfileImage(imageUrl);
            isUpdated = true;
        } else if (updateUserRequestDto != null && updateUserRequestDto.getProfileImage() == null) {
            // profileImage가 null로 요청이 들어올 때 기존의 이미지를 null로 업데이트
            userinfo.updateProfileImage(null);
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new GlobalException(USER_INFORMATION);
        }

        userRepository.save(userinfo);
        return ResponseDto.setSuccess(HttpStatus.OK, "회원 정보 수정 성공!");
    }

    // 비밀번호 변경
    public ResponseDto<String> updatePassword(Long id, UpdatePasswordRequestDto updatePasswordRequestDto, User user) {
        userRepository.findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER));

        if(!passwordEncoder.matches(updatePasswordRequestDto.getOldPassword(), user.getPassword())) {
            throw new GlobalException(BAD_REQUEST);
        }

        if(!updatePasswordRequestDto.getNewPassword().equals(updatePasswordRequestDto.getCheckNewPassword())) {
            throw new GlobalException(COINCIDE_PASSWORD);
        }

        // 비밀번호 형식이 일치하는지 체크
        Pattern passPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$");
        Matcher matcher = passPattern.matcher(updatePasswordRequestDto.getNewPassword());
        if (!matcher.find()) {
            throw new GlobalException(PASSWORD_REGEX);
        };

        user.updatePassword(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()));
        userRepository.save(user);
        return ResponseDto.setSuccess(HttpStatus.OK, "비밀번호 변경 성공!");
    }

    // 회원 탈퇴(soft, default)
    public ResponseDto<HttpStatus> deleteUser(Long id, User user) {
        userRepository.findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER));

        user.deleteUser(); // Soft delete 수행
        userRepository.save(user);

        return ResponseDto.setSuccess(HttpStatus.OK, "회원 탈퇴 성공!");
    }

    // 회원 탈퇴(hard delete)
    public ResponseDto<HttpStatus> deleteUserHard(Long id) {
        userRepository.deleteById(id);
        return ResponseDto.setSuccess(HttpStatus.OK, "영구 삭제");
    }

    private void setHeader(HttpServletResponse response, JwtTokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }
}

