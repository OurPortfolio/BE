package com.sparta.ourportfolio.portfolio.dto;

import com.sparta.ourportfolio.portfolio.enums.CategoryEnum;
import com.sparta.ourportfolio.portfolio.enums.FilterEnum;
import lombok.Getter;

@Getter
public class PortfolioRequestDto {
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
}
