package com.sparta.ourportfolio.portfolio.repository;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long>, PortfolioInquiry {
    List<Portfolio> findAllByUser_IdOrderByIdDesc(Long userId);
}
