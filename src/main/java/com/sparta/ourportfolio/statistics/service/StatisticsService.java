package com.sparta.ourportfolio.statistics.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.statistics.dto.AllStatisticsDto;
import com.sparta.ourportfolio.statistics.dto.DesignStatisticsDto;
import com.sparta.ourportfolio.statistics.dto.DevelopStatisticsDto;
import com.sparta.ourportfolio.statistics.dto.PhotographerStatisticsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final PortfolioRepository portfolioRepository;

    public ResponseDto<AllStatisticsDto> getPortfoliosAmount() {
        Map<String, Long> amountMap = portfolioRepository.getPortfoliosAmount();

        AllStatisticsDto allStatisticsDto = new AllStatisticsDto();

        for (Map.Entry<String, Long> entry : amountMap.entrySet()) {
            if (entry.getKey().equals("Develop")) {
                allStatisticsDto.setDevelop(entry.getValue());
            }
            if (entry.getKey().equals("Design")) {
                allStatisticsDto.setDesign(entry.getValue());
            }
            if (entry.getKey().equals("Photographer")) {
                allStatisticsDto.setPhotographer(entry.getValue());
            }
        }
        allStatisticsDto.setAll(allStatisticsDto.getDesign() + allStatisticsDto.getDevelop() + allStatisticsDto.getPhotographer());

        return ResponseDto.setSuccess(HttpStatus.OK, "카테고리별 개수", allStatisticsDto);
    }

    public ResponseDto<DevelopStatisticsDto> getAmountOfDevelop() {
        Map<String, Long> amountPerFilter = portfolioRepository.getPortfoliosPerFilter("Develop");

        DevelopStatisticsDto developStatisticsDto = new DevelopStatisticsDto();

        for (Map.Entry<String, Long> entry : amountPerFilter.entrySet()) {
            if (entry.getKey().equals("Backend")) {
                developStatisticsDto.setBackend(entry.getValue());
            }
            if (entry.getKey().equals("Frontend")) {
                developStatisticsDto.setFrontend(entry.getValue());
            }
            if (entry.getKey().equals("AI")) {
                developStatisticsDto.setAi(entry.getValue());
            }
            if (entry.getKey().equals("Big Data")) {
                developStatisticsDto.setBigdata(entry.getValue());
            }
            if (entry.getKey().equals("App")) {
                developStatisticsDto.setApp(entry.getValue());
            }
            if (entry.getKey().equals("System")) {
                developStatisticsDto.setSystem(entry.getValue());
            }
            if (entry.getKey().equals("Security")) {
                developStatisticsDto.setSecurities(entry.getValue());
            }
        }

        return ResponseDto.setSuccess(HttpStatus.OK, "개발자 직무별 포트폴리오", developStatisticsDto);
    }

    public ResponseDto<DesignStatisticsDto> getAmountOfDesign() {
        Map<String, Long> amountPerFilter = portfolioRepository.getPortfoliosPerFilter("Design");

        DesignStatisticsDto designStatisticsDto = new DesignStatisticsDto();

        for (Map.Entry<String, Long> entry : amountPerFilter.entrySet()) {
            if (entry.getKey().equals("Graphic")) {
                designStatisticsDto.setGraphic(entry.getValue());
            }
            if (entry.getKey().equals("UI/UX")) {
                designStatisticsDto.setUiUx(entry.getValue());
            }
            if (entry.getKey().equals("Web")) {
                designStatisticsDto.setWebs(entry.getValue());
            }
            if (entry.getKey().equals("Visual")) {
                designStatisticsDto.setVisual(entry.getValue());
            }
            if (entry.getKey().equals("Interaction")) {
                designStatisticsDto.setInteraction(entry.getValue());
            }
            if (entry.getKey().equals("Product")) {
                designStatisticsDto.setProduct(entry.getValue());
            }
            if (entry.getKey().equals("Brand")) {
                designStatisticsDto.setBrand(entry.getValue());
            }
        }

        return ResponseDto.setSuccess(HttpStatus.OK, "디자이너 직무별 포트폴리오", designStatisticsDto);
    }

    public ResponseDto<PhotographerStatisticsDto> getAmountOfPhotographer() {
        Map<String, Long> amountPerFilter = portfolioRepository.getPortfoliosPerFilter("Photographer");

        PhotographerStatisticsDto photographerStatisticsDto = new PhotographerStatisticsDto();

        for (Map.Entry<String, Long> entry : amountPerFilter.entrySet()) {
            if (entry.getKey().equals("Commercial")) {
                photographerStatisticsDto.setCommercial(entry.getValue());
            }
            if (entry.getKey().equals("Portrait")) {
                photographerStatisticsDto.setPortrait(entry.getValue());
            }
            if (entry.getKey().equals("Wedding")) {
                photographerStatisticsDto.setWedding(entry.getValue());
            }
            if (entry.getKey().equals("Fashion")) {
                photographerStatisticsDto.setFashion(entry.getValue());
            }
            if (entry.getKey().equals("Wildlife")) {
                photographerStatisticsDto.setWildlife(entry.getValue());
            }
            if (entry.getKey().equals("Sports")) {
                photographerStatisticsDto.setSports(entry.getValue());
            }
        }

        return ResponseDto.setSuccess(HttpStatus.OK, "사진작가 직무별 포트폴리오", photographerStatisticsDto);
    }


}