package com.sparta.ourportfolio.portfolio.controller;

import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @PostMapping
    public ResponseEntity<String> createPortfolio(@RequestBody PortfolioRequestDto portfolioRequestDto) {
        return portfolioService.createPortfolio(portfolioRequestDto);
    }

    @GetMapping("/{portfolio-id}")
    public ResponseEntity<PortfolioResponseDto> getPortfolio(@PathVariable(name = "portfolio-id") Long id) {
        return portfolioService.getPortfolio(id);
    }

    @PatchMapping("/{portfolio-id}")
    public ResponseEntity<String> updatePortfolio(@PathVariable(name = "portfolio-id")Long id,
                                                  @RequestBody PortfolioRequestDto portfolioRequestDto) {
        return portfolioService.updatePortfolio(id, portfolioRequestDto);
    }

    @DeleteMapping("/{portfolio-id}")
    public ResponseEntity<String> deletePortfolio(@PathVariable(name = "portfolio-id")Long id) {
        return portfolioService.deletePortfolio(id);
    }
}
