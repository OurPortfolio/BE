package com.sparta.ourportfolio.portfolio.repository;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long>, PortfolioInquiry {
}