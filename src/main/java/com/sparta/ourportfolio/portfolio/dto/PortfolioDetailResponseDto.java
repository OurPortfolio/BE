package com.sparta.ourportfolio.portfolio.dto;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.project.dto.ProjectResponseDto;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PortfolioDetailResponseDto {
    private Long id;
    private Long userId;
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
    private String portfolioImage;
    private List<ProjectResponseDto> projectList;

    public PortfolioDetailResponseDto(Portfolio portfolio) {
        this.id = portfolio.getId();
        this.userId = portfolio.getUser().getId();
        this.portfolioTitle = portfolio.getPortfolioTitle();
        this.techStack = portfolio.getTechStack();
        this.residence = portfolio.getResidence();
        this.location = portfolio.getLocation();
        this.telephone = portfolio.getTelephone();
        this.email = portfolio.getUser().getEmail();
        this.githubId = portfolio.getGithubId();
        this.experience = portfolio.getExperience();
        this.blogUrl = portfolio.getBlogUrl();
        this.category = portfolio.getCategory();
        this.filter = portfolio.getFilter();
        this.portfolioImage = portfolio.getPortfolioImage();
        this.projectList = portfolio.getProjectList().stream().map(ProjectResponseDto::new).collect(Collectors.toList());
    }
}
