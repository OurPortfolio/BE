package com.sparta.ourportfolio.portfolio.dto;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import lombok.Getter;

@Getter
public class TechStackDto {
    private String techStack;
    private Long portfolioId;

    public TechStackDto(Portfolio portfolio) {
        this.techStack = portfolio.getTechStack();
        this.portfolioId = portfolio.getId();
    }
}
