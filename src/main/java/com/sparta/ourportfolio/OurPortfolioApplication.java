package com.sparta.ourportfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class OurPortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(OurPortfolioApplication.class, args);
    }

}
