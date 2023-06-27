package com.sparta.ourportfolio.portfolio.repository;

import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long>, PortfolioInquiry {
    List<Portfolio> findAllByUserIdOrderByIdDesc(Long userId);
}
