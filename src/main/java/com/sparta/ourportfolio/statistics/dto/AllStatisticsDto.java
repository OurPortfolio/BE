package com.sparta.ourportfolio.statistics.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AllStatisticsDto {

    private Long all;
    private Long develop;
    private Long design;
    private Long photographer;

    public void setDesign(Long design) {
        this.design = design;
    }

    public void setAll(Long all) {
        this.all = all;
    }

    public void setDevelop(Long develop) {
        this.develop = develop;
    }

    public void setPhotographer(Long photographer) {
        this.photographer = photographer;
    }
}
