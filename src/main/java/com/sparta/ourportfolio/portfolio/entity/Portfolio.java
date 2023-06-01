package com.sparta.ourportfolio.portfolio.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.user.entity.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PORTFOLIO_ID")
    private Long id;

    @Column(nullable = false)
    private String portfolioTitle;

    @Column(nullable = false)
    private String techStack;

    private String residence;

    private String location;

    private String telephone;

    private String githubId;

    private String experience;

    private String blogUrl;

    private String category;

    private String filter;

    private String youtubeUrl;

    @Column(columnDefinition = "LONGTEXT")
    @Lob
    private String portfolioImage;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Project> projectList = new ArrayList<>();

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    public Portfolio(PortfolioRequestDto portfolioRequestDto, @Nullable String imageUrl) {
        this.portfolioTitle = portfolioRequestDto.getPortfolioTitle();
        this.techStack = portfolioRequestDto.getTechStack();
        this.residence = portfolioRequestDto.getResidence();
        this.location = portfolioRequestDto.getLocation();
        this.telephone = portfolioRequestDto.getTelephone();
        this.githubId = portfolioRequestDto.getGithubId();
        this.experience = portfolioRequestDto.getExperience();
        this.blogUrl = portfolioRequestDto.getBlogUrl();
        this.category = portfolioRequestDto.getCategory();
        this.filter = portfolioRequestDto.getFilter();
        this.youtubeUrl = portfolioRequestDto.getYoutubeUrl();
        this.portfolioImage = imageUrl;
    }

    public void update(PortfolioRequestDto portfolioRequestDto, String imageUrl) {
        this.portfolioTitle = portfolioRequestDto.getPortfolioTitle();
        this.techStack = portfolioRequestDto.getTechStack();
        this.residence = portfolioRequestDto.getResidence();
        this.location = portfolioRequestDto.getLocation();
        this.telephone = portfolioRequestDto.getTelephone();
        this.githubId = portfolioRequestDto.getGithubId();
        this.experience = portfolioRequestDto.getExperience();
        this.blogUrl = portfolioRequestDto.getBlogUrl();
        this.category = portfolioRequestDto.getCategory();
        this.filter = portfolioRequestDto.getFilter();
        this.youtubeUrl = portfolioRequestDto.getYoutubeUrl();
        this.portfolioImage = imageUrl;
    }

    public void addProject(Project project) {
        this.projectList.add(project);
    }

    public void setUser(User user) {
        this.user = user;
    }
}
