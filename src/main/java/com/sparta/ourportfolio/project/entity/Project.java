package com.sparta.ourportfolio.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.ourportfolio.project.dto.ProjectRequestDto;
import com.sparta.ourportfolio.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project-id")
    private Long id;

    @Column
    private String title;

    @Column
    private String term;

    @Column
    private String people;

    @Column
    private String position;

    @Column
    private String description;

    @JsonManagedReference
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    private List<ProjectImage> projectImageList;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

//    @JsonIgnore
//    @ManyToOne
//    private Portfolio portfolio;

    @Builder
    public Project(ProjectRequestDto projectRequestDto, User user) {
        this.title = projectRequestDto.getTitle();
        this.term = projectRequestDto.getTerm();
        this.people = projectRequestDto.getPeople();
        this.position = projectRequestDto.getPosition();
        this.description = projectRequestDto.getDescription();
        this.user = user;
    }

    public void updateProject(ProjectRequestDto projectRequestDto) {
        this.title = projectRequestDto.getTitle();
        this.term = projectRequestDto.getTerm();
        this.people = projectRequestDto.getPeople();
        this.position = projectRequestDto.getPosition();
        this.description = projectRequestDto.getDescription();
    }

    public void setImageFile(List<ProjectImage> projectImageList){
        this.projectImageList = projectImageList;
    }

//    private void update(ProjectRequestDto projectRequestDto) {
//        this.title = projectRequestDto.getTitle();
//        this.term = projectRequestDto.getTerm();
//        this.people = projectRequestDto.getPeople();
//        this.position = projectRequestDto.getPosition();
//        this.description = projectRequestDto.getDescription();
//    }
}
