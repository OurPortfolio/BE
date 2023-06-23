package com.sparta.ourportfolio.user.email.controller;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.user.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/api/users/email")
    public ResponseDto<String> emailConfirm(@RequestParam String receiverEmail) throws Exception {

        return emailService.sendSimpleMessage(receiverEmail);
    }
}