package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

    public ResponseEntity<String> createPortfolio() {
        //유저 확인
        Portfolio portfolio = new Portfolio();
        portfolioRepository.saveAndFlush(portfolio);

        return ResponseEntity.ok().body("포트폴리오 생성 완료");
    }
}
