package com.sparta.ourportfolio.portfolio.dto;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.project.dto.ProjectResponseDto;
import com.sparta.ourportfolio.project.entity.Project;
import lombok.Getter;

import java.util.List;

@Getter
public class PortfolioDetailResponseDto {
    private Long id;
    private Long userId;
    private String profileImage;
    private String portfolioTitle;
    private String intro;
    private String techStack;
    private String residence;
    private String location;
    private String telephone;
    private String email;
    private String githubId;
    private List<String> experience;
    private String blogUrl;
    private String category;
    private String filter;
    private String portfolioImage;
    private String youtubeUrl;
    private List<ProjectResponseDto> projectList;
    private long views;

    public PortfolioDetailResponseDto(Portfolio portfolio) {
        this.id = portfolio.getId();
        this.userId = portfolio.getUser().getId();
        this.profileImage = portfolio.getUser().getProfileImage();
        this.portfolioTitle = portfolio.getPortfolioTitle();
        this.intro = portfolio.getIntro();
        this.techStack = portfolio.getTechStack();
        this.residence = portfolio.getResidence();
        this.location = portfolio.getLocation();
        this.telephone = portfolio.getTelephone();
        this.email = portfolio.getEmail();
        this.githubId = portfolio.getGithubId();
        this.experience = portfolio.getProjectList().stream().map(Project::getTitle).toList();
        this.blogUrl = portfolio.getBlogUrl();
        this.category = portfolio.getCategory();
        this.filter = portfolio.getFilter();
        this.portfolioImage = portfolio.getPortfolioImage();
        this.youtubeUrl = portfolio.getYoutubeUrl();
        this.projectList = portfolio.getProjectList().stream().map(ProjectResponseDto::new).toList();
        this.views = portfolio.getViews();
    }
}
