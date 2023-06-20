package com.sparta.ourportfolio.statistics.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DevelopStatisticsDto {
    private Long backend;
    private Long frontend;
    private Long ai;
    private Long bigdata;
    private Long app;
    private Long system;
    private Long security;

    public void setAi(Long ai) {
        this.ai = ai;
    }

    public void setApp(Long app) {
        this.app = app;
    }

    public void setBackend(Long backend) {
        this.backend = backend;
    }

    public void setBigdata(Long bigdata) {
        this.bigdata = bigdata;
    }

    public void setFrontend(Long frontend) {
        this.frontend = frontend;
    }

    public void setSecurities(Long security) {
        this.security = security;
    }

    public void setSystem(Long system) {
        this.system = system;
    }
}
