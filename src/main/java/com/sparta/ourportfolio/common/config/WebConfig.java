package com.sparta.ourportfolio.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowedOrigins("https://ppol.pro")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // 허용할 HTTP method
                .allowCredentials(true);
    }
}
