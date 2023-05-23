package com.sparta.ourportfolio.portfolio.entity;

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

    private String blogUrl;

    private FilterEnum filter;

    private CategoryEnum category;
}
