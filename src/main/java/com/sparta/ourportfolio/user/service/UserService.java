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

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$";
    private static final String NICKNAME_PATTERN = "^[a-zA-Z가-힣0-9]{1,10}$";

    //회원가입
    public ResponseDto<HttpStatus> signup(SignupRequestDto signupRequestDto) {
        validateEmail(signupRequestDto.getEmail());
        validatePassword(signupRequestDto.getPassword());
        validateNickname(signupRequestDto.getNickname());

        String password = passwordEncoder.encode(signupRequestDto.getPassword());

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

        //이메일, 비밀번호 패턴 검사
        validateEmail(email);
        validatePassword(password);

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER));

        if (user.isDeleted()) {
            throw new GlobalException(USER_IS_DELETED);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new GlobalException(BAD_PASSWORD);
        }

        JwtTokenDto tokenDto = jwtUtil.createAllToken(email, user.getId());
        setRefreshToken(response, tokenDto.getRefreshToken(), user.getEmail());

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
                                          MultipartFile image, User user) throws IOException {
        User userinfo = userRepository.findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER));

        if (!StringUtils.equals(user.getId(), userinfo.getId())) {
            throw new GlobalException(UNAUTHORIZED);
        }

        boolean isUpdated = false;

        // 닉네임 수정
        if (updateUserRequestDto != null && updateUserRequestDto.getNickname().isEmpty()) {
            String newNickname = updateUserRequestDto.getNickname();
            updateNickname(userinfo, newNickname);
            isUpdated = true;
        }

        // 업로드한 이미지로 업데이트
        if (image != null && !user.getProfileImage().isEmpty()) {
            updateProfileImage(userinfo, image);
            isUpdated = true;
        } else if (updateUserRequestDto != null && updateUserRequestDto.getProfileImage() == null) {
            // profileImage 가 null 로 요청이 들어올 때 기존의 이미지를 null 로 업데이트
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

        if (!passwordEncoder.matches(updatePasswordRequestDto.getOldPassword(), user.getPassword())) {
            throw new GlobalException(PRESENT_PASSWORD);
        }

        if (!updatePasswordRequestDto.getNewPassword().equals(updatePasswordRequestDto.getCheckNewPassword())) {
            throw new GlobalException(COINCIDE_PASSWORD);
        }

        validatePassword(updatePasswordRequestDto.getNewPassword()); // 비밀번호 패턴 검사

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
    public ResponseDto<HttpStatus> deleteUserHard(Long id, User user) {
        userRepository.deleteById(user.getId());
        return ResponseDto.setSuccess(HttpStatus.OK, "영구 삭제");
    }

    private void setHeader(HttpServletResponse response, JwtTokenDto tokenDto) {
        response.addHeader(JwtUtil.ACCESS_TOKEN, tokenDto.getAccessToken());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefreshToken());
    }

    private void setRefreshToken(HttpServletResponse response, String refreshToken, String email) {
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByEmail(email);
        RefreshToken newToken;
        if (existingToken.isPresent()) {
            newToken = existingToken.get().updateToken(refreshToken);
        } else {
            newToken = new RefreshToken(refreshToken, email);
        }
        refreshTokenRepository.save(newToken);
    }

    //이메일 중복 검사
    public ResponseDto<Boolean> checkEmail(String email) {
        validateEmail(email);

        boolean exists = userRepository.existsByEmail(email);
        if (exists) {
            throw new GlobalException(DUPLICATED_EMAIL);
        }
        return ResponseDto.setSuccess(HttpStatus.OK, "이메일이 중복되지 않습니다.");
    }

    //이메일 패턴 검사
    private void validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new GlobalException(EMAIL_REGEX);
        }
    }

    //비밀번호 패턴 검사
    private void validatePassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            throw new GlobalException(PASSWORD_REGEX);
        }
    }

    //닉네임 패턴 검사
    private void validateNickname(String nickname) {
        Pattern pattern = Pattern.compile(NICKNAME_PATTERN);
        Matcher matcher = pattern.matcher(nickname);
        if (!matcher.matches()) {
            throw new GlobalException(NICKNAME_REGEX);
        }
    }

    // 닉네임 업데이트 메서드
    private void updateNickname(User user, String newNickname) {
        if (!newNickname.equals(user.getNickname()) && userRepository.existsByNickname(newNickname)) {
            throw new GlobalException(DUPLICATED_NICK_NAME);
        }
        validateNickname(newNickname); // 닉네임 패턴 검사
        user.updateNickname(newNickname);
    }

    // 프로필 이미지 업데이트 메서드
    private void updateProfileImage(User user, MultipartFile image) throws IOException {
        String imageUrl = s3Service.uploadFile(image);
        user.updateProfileImage(imageUrl);
    }
}