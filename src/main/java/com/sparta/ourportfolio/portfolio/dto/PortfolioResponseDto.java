package com.sparta.ourportfolio.portfolio.dto;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import lombok.Getter;

@Getter
public class PortfolioResponseDto {
    private Long portfolioId;
    private String portfolioImage;
    //유저 이미지 추가
    private String userName;
    private String category;
    private String filter;

    public PortfolioResponseDto(Portfolio portfolio) {
        this.portfolioId = portfolio.getId();
        this.portfolioImage = portfolio.getPortfolioImage();
        this.userName = "cozyboy";
        this.category = portfolio.getCategory();
        this.filter = portfolio.getFilter();
    }
}
