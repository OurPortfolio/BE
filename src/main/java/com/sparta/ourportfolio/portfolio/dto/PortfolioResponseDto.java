package com.sparta.ourportfolio.portfolio.dto;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import lombok.Getter;

@Getter
public class PortfolioResponseDto {
    private Long id;
    private String portfolioTitle;
    private String portfolioImage;
    //유저 이미지 추가
    private String userName;
    private String category;
    private String filter;

    public PortfolioResponseDto(Portfolio portfolio) {
        this.id = portfolio.getId();
        this.portfolioTitle = portfolio.getPortfolioTitle();
        this.portfolioImage = portfolio.getPortfolioImage();
        this.userName = "cozyboy";
        this.category = portfolio.getCategory();
        this.filter = portfolio.getFilter();
    }
}
