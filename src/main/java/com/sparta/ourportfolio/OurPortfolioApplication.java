package com.sparta.ourportfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@PropertySource(value = {"classpath:application-aws.properties"})
public class OurPortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(OurPortfolioApplication.class, args);
    }

}
