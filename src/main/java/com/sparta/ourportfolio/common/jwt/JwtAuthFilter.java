package com.sparta.ourportfolio.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ourportfolio.common.security.SecurityExceptionDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {

        // JWT 토큰을 해석하여 추출
        String access_token = jwtUtil.resolveToken(request, JwtUtil.ACCESS_TOKEN);
        String refresh_token = jwtUtil.resolveToken(request, JwtUtil.REFRESH_TOKEN);

        // 토큰이 존재하면 유효성 검사를 수행하고, 유효하지 않은 경우 예외 처리
        if (access_token == null) {
            filterChain.doFilter(request, response);
        } else {
            if (jwtUtil.validateToken(access_token)) {
                setAuthentication(jwtUtil.getUserInfoFromToken(access_token));
            } else if (refresh_token != null && jwtUtil.refreshTokenValid(refresh_token)) {
                //Refresh토큰으로 유저명 가져오기
                String userEmail = jwtUtil.getUserInfoFromToken(refresh_token);
                //유저명으로 유저 정보 가져오기
                User user = userRepository.findByEmail(userEmail).get();
                //새로운 ACCESS TOKEN 발급
                String newAccessToken = jwtUtil.createToken(userEmail, "Access", user.getId());
                //Header에 ACCESS TOKEN 추가
                jwtUtil.setHeaderAccessToken(response, newAccessToken);
                setAuthentication(userEmail);
            } else if (refresh_token == null) {
                jwtExceptionHandler(response, "AccessToken이 만료되었습니다.", HttpStatus.BAD_REQUEST.value());
                return;
            } else {
                jwtExceptionHandler(response, "RefreshToken이 만료되었습니다. 다시 로그인 해주세요.", HttpStatus.BAD_REQUEST.value());
                return;
            }
            // 다음 필터로 요청과 응답을 전달하여 필터 체인 계속 실행
            filterChain.doFilter(request, response);
        }
    }

    // 인증 객체를 생성하여 SecurityContext에 설정
    public void setAuthentication(String userEmail) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtUtil.createAuthentication(userEmail);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // JWT 예외 처리를 위한 응답 설정
    public void jwtExceptionHandler(HttpServletResponse response, String message, int statusCode) {
        response.setStatus(statusCode);
        response.setContentType("application/json; charset=utf8"); //한국어 깨짐 문제 해결
        try {
            // 예외 정보를 JSON 형태로 변환하여 응답에 작성
            String json = new ObjectMapper().writeValueAsString(new SecurityExceptionDto(statusCode, message));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}