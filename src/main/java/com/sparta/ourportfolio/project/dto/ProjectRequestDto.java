package com.sparta.ourportfolio.project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRequestDto {
    private String title;
    private String term;
    private String people;
    private String position;
    private String description;
}
