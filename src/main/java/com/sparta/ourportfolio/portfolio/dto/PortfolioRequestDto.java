package com.sparta.ourportfolio.portfolio.dto;

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
    private String email;
    private String githubId;
    private String blogUrl;
    private String category;
    private String filter;
    private String youtubeUrl;
    private List<Long> projectIdList;
}
