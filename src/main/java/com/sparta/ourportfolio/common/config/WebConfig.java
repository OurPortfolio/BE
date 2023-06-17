package com.sparta.ourportfolio.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.sparta.ourportfolio.common.jwt.JwtUtil.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .exposedHeaders(ACCESS_TOKEN,REFRESH_TOKEN,AUTHORIZATION_HEADER)
                .allowedOrigins(
                        "https://ppol.pro",
                        "https://portfol.pro"
                )
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://ppol.pro.s3-website-us-east-1.amazonaws.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP method
                .allowCredentials(false);
    }
}
