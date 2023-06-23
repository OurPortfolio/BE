package com.sparta.ourportfolio.user.email.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

import static com.sparta.ourportfolio.common.exception.ExceptionEnum.DUPLICATED_EMAIL;
import static com.sparta.ourportfolio.common.exception.ExceptionEnum.EMAIL_BAD_SEND;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;
    private final UserRepository userRepository;

    public ResponseDto<String> sendSimpleMessage(String receiverEmail) throws Exception {
        Optional<User> findUserByEmail = userRepository.findByEmail(receiverEmail);
        if (findUserByEmail.isPresent()) {
            throw new GlobalException(DUPLICATED_EMAIL);
        }

        String verificationCode = createCode();
        MimeMessage message = createMessage(receiverEmail, verificationCode);
        try {
            emailSender.send(message);
        } catch (MailException es) {
            throw new GlobalException(EMAIL_BAD_SEND);
        }
        log.info("인증 코드 : " + verificationCode);
        return ResponseDto.setSuccess(HttpStatus.OK, "메일 전송에 성공하였습니다.");
    }

    private MimeMessage createMessage(String receiverEmail, String verificationCode) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(RecipientType.TO, receiverEmail);
        message.setSubject("[POL] 회원가입 이메일 인증번호");
        String msg = "";
        msg += "<div style=\"padding: 26px 18px;\">";
        msg += "<a href=\"https://imgbb.com/\"><img src=\"https://i.ibb.co/9430yQb/Group-204.png\" alt=\"Group-204\" style=\"width: 248px; height: 92px;\" loading=\"lazy\"></a>";
        msg += "<h1 style=\"margin-top: 23px; margin-bottom: 9px; color: #222222; font-size: 19px; line-height: 25px; letter-spacing: -0.27px;\">이메일 인증</h1>";
        msg += "<div style=\"margin-top: 7px; margin-bottom: 22px; color: #222222;\">";
        msg += "<p style=\"margin-block-start: 0; margin-block-end: 0; margin-inline-start: 0; margin-inline-end: 0; line-height: 1.47; letter-spacing: -0.22px; font-size: 15px; margin: 8px 0 0;\">안녕하세요.</p>";
        msg += "<p style=\"margin-block-start: 0; margin-block-end: 0; margin-inline-start: 0; margin-inline-end: 0; line-height: 1.47; letter-spacing: -0.22px; font-size: 15px; margin: 8px 0 0; color: #6BF65F;\">POL</p>";
        msg += "<p style=\"margin-block-start: 0; margin-block-end: 0; margin-inline-start: 0; margin-inline-end: 0; line-height: 1.47; letter-spacing: -0.22px; font-size: 15px; margin: 8px 0 0;\">입니다.</p>";
        msg += "<p style=\"margin-block-start: 0; margin-block-end: 0; margin-inline-start: 0; margin-inline-end: 0; line-height: 1.47; letter-spacing: -0.22px; font-size: 15px; margin: 8px 0 0;\">아래의 확인 코드를 회원가입 이메일 인증칸에 입력해주세요.</p>";
        msg += "<p style=\"margin-block-start: 0; margin-block-end: 0; margin-inline-start: 0; margin-inline-end: 0; line-height: 1.47; letter-spacing: -0.22px; font-size: 15px; margin: 8px 0 0;\">";
        msg += "<h1>" + verificationCode + "</h1></p>";
        msg += "<p style=\"margin-block-start: 0; margin-block-end: 0; margin-inline-start: 0; margin-inline-end: 0; line-height: 1.47; letter-spacing: -0.22px; font-size: 15px; margin: 20px 0;\">";
        msg += "감사합니다.<br>";
        msg += "POL 팀 드림</p>";
        msg += "<hr style=\"display: block; height: 1px; background-color: #ebebeb; margin: 14px 0; padding: 0; border-width: 0;\"><div><div>";
        msg += "<p style=\"margin-block: 0; margin-inline: 0; font-weight: normal; font-size: 14px; font-stretch: normal; font-style: normal; line-height: 1.43; letter-spacing: normal; color: #8a8a8a; margin: 5px 0 0;\">본 메일은 발신전용 메일로 회신되지 않습니다. 본 메일과 관련되어 궁금하신 점이나 불편한 사항은 고객센터에 문의해 주시기 바랍니다.</p></div>";
        msg += "<div><p style=\"margin-block: 0; margin-inline: 0; font-weight: normal; font-size: 14px; font-stretch: normal; font-style: normal; line-height: 1.43; letter-spacing: normal; color: #8a8a8a; margin: 5px 0 0;\">주식회사 POL | 많기도 많군 둘러보면 놀라자빠지리 888, 88층 | pol@pol.com<br>";
        msg += "전화번호: 02-888-8888 | 통신판매업 신고번호: 제 2023-폴-8888호<br>";
        msg += "Copyright © 2023 by <b>POL, Inc.</b> All rights reserved.</p></div></div></div>";
        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress("kmg6381@naver.com", "POL"));

        return message;
    }

    // 인증코드 만들기
    @SneakyThrows
    public String createCode() {
        StringBuilder code = new StringBuilder();
        Random randomNum = SecureRandom.getInstanceStrong();

        for (int i = 0; i < 6; i++) {
            code.append((randomNum.nextInt(10)));
        }
        return code.toString();
    }

}
