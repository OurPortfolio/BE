package com.sparta.ourportfolio.portfolio.entity;

import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.enums.CategoryEnum;
import com.sparta.ourportfolio.portfolio.enums.FilterEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PORTFOLIO_ID")
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

    public Portfolio(PortfolioRequestDto portfolioRequestDto) {
        this.title = portfolioRequestDto.getTitle();
        this.techStack = portfolioRequestDto.getTechStack();
        this.residence = portfolioRequestDto.getResidence();
        this.location = portfolioRequestDto.getLocation();
        this.telephone = portfolioRequestDto.getTelephone();
        this.email = portfolioRequestDto.getEmail();
        this.githubId = portfolioRequestDto.getGithubId();
        this.experience = portfolioRequestDto.getExperience();
        this.blogUrl = portfolioRequestDto.getBlogUrl();
        this.category = portfolioRequestDto.getCategory();
        this.filter = portfolioRequestDto.getFilter();
    }
    public void update(PortfolioRequestDto portfolioRequestDto) {
        this.title = portfolioRequestDto.getTitle();
        this.techStack = portfolioRequestDto.getTechStack();
        this.residence = portfolioRequestDto.getResidence();
        this.location = portfolioRequestDto.getLocation();
        this.telephone = portfolioRequestDto.getTelephone();
        this.email = portfolioRequestDto.getEmail();
        this.githubId = portfolioRequestDto.getGithubId();
        this.experience = portfolioRequestDto.getExperience();
        this.blogUrl = portfolioRequestDto.getBlogUrl();
        this.category = portfolioRequestDto.getCategory();
        this.filter = portfolioRequestDto.getFilter();
    }
}
