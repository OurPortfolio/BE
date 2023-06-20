package com.sparta.ourportfolio.statistics.controller;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.statistics.dto.AllStatisticsDto;
import com.sparta.ourportfolio.statistics.dto.DesignStatisticsDto;
import com.sparta.ourportfolio.statistics.dto.DevelopStatisticsDto;
import com.sparta.ourportfolio.statistics.dto.PhotographerStatisticsDto;
import com.sparta.ourportfolio.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseDto<AllStatisticsDto> getPortfoliosAmount() {
        return statisticsService.getPortfoliosAmount();
    }

    @GetMapping("/develop")
    public ResponseDto<DevelopStatisticsDto> getAmountOfDevelop() {
        return statisticsService.getAmountOfDevelop();
    }

    @GetMapping("/design")
    public ResponseDto<DesignStatisticsDto> getAmountOfDesign() {
        return statisticsService.getAmountOfDesign();
    }

    @GetMapping("/photographer")
    public ResponseDto<PhotographerStatisticsDto> getAmountOfPhotographer() {
        return statisticsService.getAmountOfPhotographer();
    }
}
