package com.sparta.ourportfolio.project.dto;

import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.entity.ProjectImage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProjectResponseDto {
    private String title;
    private String term;
    private String people;
    private String position;
    private String description;
    private List<ProjectImage> projectImageList;

    @Builder
    public ProjectResponseDto(Project project) {
        this.title = project.getTitle();
        this.term = project.getTerm();
        this.people = project.getPeople();
        this.position = project.getPosition();
        this.description = project.getDescription();
        this.projectImageList = project.getProjectImageList();

    }
}
