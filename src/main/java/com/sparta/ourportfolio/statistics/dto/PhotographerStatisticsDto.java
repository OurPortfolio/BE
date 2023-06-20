package com.sparta.ourportfolio.statistics.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhotographerStatisticsDto {
    private Long commercial;
    private Long portrait;
    private Long wedding;
    private Long fashion;
    private Long wildlife;
    private Long sports;

    public void setCommercial(Long commercial) {
        this.commercial = commercial;
    }

    public void setPortrait(Long portrait) {
        this.portrait = portrait;
    }

    public void setWedding(Long wedding) {
        this.wedding = wedding;
    }

    public void setFashion(Long fashion) {
        this.fashion = fashion;
    }

    public void setSports(Long sports) {
        this.sports = sports;
    }

    public void setWildlife(Long wildlife) {
        this.wildlife = wildlife;
    }
}
