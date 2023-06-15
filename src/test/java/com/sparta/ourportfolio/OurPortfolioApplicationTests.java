package com.sparta.ourportfolio;

import com.sparta.ourportfolio.user.dto.SignupRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OurPortfolioApplicationTests {

    @Test
    void contextLoads() {
        SignupRequestDto signupRequestDto1 = new SignupRequestDto("test1234@example.com", "test1234", "test1234", null);
        SignupRequestDto signupRequestDto2 = new SignupRequestDto("test1234@example.com", "test1234", "test1234", null);
        assert signupRequestDto1.equals(signupRequestDto2);
    }

}
