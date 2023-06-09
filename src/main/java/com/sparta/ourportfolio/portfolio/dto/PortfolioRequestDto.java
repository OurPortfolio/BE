package com.sparta.ourportfolio.portfolio.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PortfolioRequestDto {
    private String portfolioTitle;
    private String intro;
    private String techStack;
    private String residence;
    private String location;
    private String telephone;
    private String githubId;
    private String blogUrl;
    private String category;
    private String filter;
    private String youtubeUrl;
    private List<Long> projectIdList;

    @Builder
    public PortfolioRequestDto(String portfolioTitle, String intro, String techStack, String residence,
                               String location, String telephone, String email, String githubId, String blogUrl,
                               String category, String filter, String youtubeUrl, List<Long> projectIdList) {
        this.portfolioTitle = portfolioTitle;
        this.intro = intro;
        this.techStack = techStack;
        this.residence = residence;
        this.location = location;
        this.telephone = telephone;
        this.githubId = githubId;
        this.blogUrl = blogUrl;
        this.category = category;
        this.filter = filter;
        this.youtubeUrl = youtubeUrl;
        this.projectIdList = projectIdList;
    }
}
