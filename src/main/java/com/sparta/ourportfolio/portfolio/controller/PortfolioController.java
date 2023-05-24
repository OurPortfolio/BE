package com.sparta.ourportfolio.portfolio.controller;

import com.sparta.ourportfolio.portfolio.dto.FilterRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioDetailResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<String> createPortfolio(@RequestPart(name = "portfolioRequestDto")
                                                  PortfolioRequestDto portfolioRequestDto,
                                                  @RequestPart(name = "portfolioImage") MultipartFile image) {
        return portfolioService.createPortfolio(portfolioRequestDto, image);
    }

    @GetMapping("/{portfolio-id}")
    public ResponseEntity<PortfolioDetailResponseDto> getPortfolio(@PathVariable(name = "portfolio-id") Long id) {
        return portfolioService.getPortfolio(id);
    }

    @GetMapping
    public ResponseEntity<Slice<PortfolioResponseDto>> getAllPortfolios(@RequestParam(name = "last-portfolio-id") Long id,
                                                                        @RequestParam(name = "size") int size) {
        return portfolioService.getAllPortfolios(id, size);
    }

    @PatchMapping("/{portfolio-id}")
    public ResponseEntity<String> updatePortfolio(@PathVariable(name = "portfolio-id") Long id,
                                                  @RequestBody PortfolioRequestDto portfolioRequestDto) {
        return portfolioService.updatePortfolio(id, portfolioRequestDto);
    }

    @DeleteMapping("/{portfolio-id}")
    public ResponseEntity<String> deletePortfolio(@PathVariable(name = "portfolio-id") Long id) {
        return portfolioService.deletePortfolio(id);
    }
}
