package com.sparta.ourportfolio.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProjectRequestDto {
    private String title;
    private String term;
    private String people;
    private String position;
    private String description;

    @Builder
    public ProjectRequestDto(String title, String term, String people, String position, String description) {
        this.title = title;
        this.term = term;
        this.people = people;
        this.position = position;
        this.description = description;
    }
}
