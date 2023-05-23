package com.sparta.ourportfolio.portfolio.dto;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.enums.CategoryEnum;
import com.sparta.ourportfolio.portfolio.enums.FilterEnum;
import lombok.Getter;

@Getter
public class PortfolioResponseDto {
    private Long id;
    private String title;
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

    public PortfolioResponseDto(Portfolio portfolio) {
        this.id = portfolio.getId();
        this.title = portfolio.getTitle();
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
