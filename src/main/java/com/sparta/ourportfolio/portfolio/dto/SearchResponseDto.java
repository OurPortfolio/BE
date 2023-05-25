package com.sparta.ourportfolio.portfolio.dto;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import lombok.Getter;

@Getter
public class SearchResponseDto {
    private Long id;
    private String portfolioTitle;
    private String techStack;

    public SearchResponseDto(Portfolio portfolio) {
        this.id = portfolio.getId();
        this.portfolioTitle = portfolio.getPortfolioTitle();
        this.techStack = portfolio.getTechStack();
    }
}
