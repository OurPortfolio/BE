package com.sparta.ourportfolio.portfolio.dto;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import lombok.Getter;

@Getter
public class PortfolioDetailResponseDto {
    private Long id;
    private String portfolioTitle;
    private String techStack;
    private String residence;
    private String location;
    private String telephone;
    private String email;
    private String githubId;
    private String experience;
    private String blogUrl;
    private String category;
    private String filter;

    public PortfolioDetailResponseDto(Portfolio portfolio) {
        this.id = portfolio.getId();
        this.portfolioTitle = portfolio.getPortfolioTitle();
        this.techStack = portfolio.getTechStack();
        this.residence = portfolio.getResidence();
        this.location = portfolio.getLocation();
        this.telephone = portfolio.getTelephone();
        this.email = portfolio.getEmail();
        this.githubId = portfolio.getGithubId();
        this.experience = portfolio.getExperience();
        this.blogUrl = portfolio.getBlogUrl();
        this.category = portfolio.getCategory();
        this.filter = portfolio.getFilter();
    }
}
