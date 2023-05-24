package com.sparta.ourportfolio.portfolio.repository;

import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @Query(value = "select new com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto(p) from Portfolio p where p.id < ?1 order by p.id desc")
    Slice<PortfolioResponseDto> findByIdLessThanOrderByPostIdDesc(Long lastPostId, PageRequest pageRequest);
}
