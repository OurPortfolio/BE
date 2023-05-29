package com.sparta.ourportfolio.user.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.jwt.JwtTokenDto;
import com.sparta.ourportfolio.common.jwt.JwtUtil;
import com.sparta.ourportfolio.common.jwt.refreshToken.RefreshToken;
import com.sparta.ourportfolio.common.jwt.refreshToken.RefreshTokenRepository;
import com.sparta.ourportfolio.user.dto.LoginRequestDto;
import com.sparta.ourportfolio.user.dto.SignupRequestDto;
import com.sparta.ourportfolio.user.dto.UpdateUserRequestDto;
import com.sparta.ourportfolio.user.dto.UserDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    //회원 조회
    public ResponseDto<UserDto> getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getNickname(), user.getProfileImage(), user.getKakaoId());
        return ResponseDto.setSuccess(HttpStatus.OK, "회원 조회 성공!", userDto);
    }

//    @Transactional
//    public ResponseDto<String> updateUser(Long id, UpdateUserRequestDto updateUserRequestDto, User user) {
//        // 유저 엔티티 가져오기
//        Optional<User> getUser = userRepository.findByIdAndDeletedFalse(id);
//        // 유저 엔티티가 없으면 에러
//        if(getUser.isEmpty()){
//            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
//        }
//
//        User userUpdate = getUser.get();
//        //유저 확인
//        if (!userUpdate.getId().equals(user.getId())) {
//            throw new IllegalArgumentException("권한이 없습니다.");
//        }
//
//        boolean nicknameUpdated = false;
//        boolean passwordUpdated = false;
//
//        String newNickname = updateUserRequestDto.getNickname();
//        if (newNickname != null && !newNickname.isEmpty()) {
//            // 닉네임이 현재와 같은지 체크
//            if(newNickname.equals((userUpdate.getNickname()))){
//                throw new IllegalArgumentException("닉네임이 동일합니다.");
//            }
//            // 중복된 닉네임이 있는지 체크
//            if(userRepository.existsByNickname(newNickname)) {
//                throw new IllegalArgumentException("해당 닉네임이 존재합니다.");
//            }
//            // 닉네임 형식이 일치하는지 체크
//            Pattern passPattern1 = Pattern.compile("^[a-zA-Z가-힣0-9]{1,10}$");
//            Matcher matcher1 = passPattern1.matcher(newNickname);
//            if(!matcher1.find()) {
//                throw new IllegalArgumentException("닉네임을 형식에 맞춰 올바르게 입력바랍니다.");
//            };
//            nicknameUpdated = true;
//        }
//
//        String newPassword = updateUserRequestDto.getPassword();
//        if (newPassword != null && !newPassword.isEmpty()) {
//            // 패스워드가 현재와 같은지 체크
//            String encodePassword = userUpdate.getPassword();
//            if(passwordEncoder.matches(newPassword, encodePassword)) {
//                throw new IllegalArgumentException("패스워드가 동일합니다.");
//            }
//            // 비밀번호 형식이 일치하는지 체크
//            Pattern passPattern2 = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,}$");
//            Matcher matcher2 = passPattern2.matcher(newPassword);
//            if (!matcher2.find()) {
//                throw new IllegalArgumentException("패스워드를 형식에 맞춰 올바르게 입력바랍니다.");
//            };
//            newPassword = passwordEncoder.encode(newPassword); // 패스워드 암호화
//            passwordUpdated = true;
//        }
//
//        // 변경한 값이 없을 때
//        if (!nicknameUpdated && !passwordUpdated) {
//            throw new IllegalArgumentException("변경할 닉네임이나 패스워드를 입력해주세요.");
//        }
//
//        // 유저 정보 업데이트 및 저장
//        if (nicknameUpdated) userUpdate.updateNickname(newNickname);
//        if (passwordUpdated) userUpdate.updatePassword(newPassword);
//        userRepository.save(userUpdate);
//
//        return ResponseDto.setSuccess(HttpStatus.OK, "회원 정보 수정 성공!");
//    }

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

