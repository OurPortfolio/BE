package com.sparta.ourportfolio.statistics.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DesignStatisticsDto {
    private Long graphic;
    private Long uiUx;
    private Long web;
    private Long visual;
    private Long interaction;
    private Long product;
    private Long brand;

    public void setGraphic(Long graphic) {
        this.graphic = graphic;
    }

    public void setUiUx(Long uiUx) {
        this.uiUx = uiUx;
    }

    public void setWebs(Long web) {
        this.web = web;
    }

    public void setVisual(Long visual) {
        this.visual = visual;
    }

    public void setInteraction(Long interaction) {
        this.interaction = interaction;
    }

    public void setProduct(Long product) {
        this.product = product;
    }

    public void setBrand(Long brand) {
        this.brand = brand;
    }
}
