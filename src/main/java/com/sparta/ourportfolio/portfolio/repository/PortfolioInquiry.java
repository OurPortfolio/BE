package com.sparta.ourportfolio.portfolio.repository;

import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.dto.TechStackDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface PortfolioInquiry {
    Slice<PortfolioResponseDto> getPortfolios(Long lastPortfolioId, PageRequest pageRequest, String category,
                                              String filter);

    Page<PortfolioResponseDto> searchPortfolios(Pageable pageable, String keyword);

    Long getLastPortfolioIdByCategoryAndFilter(String category, String filter);

    Map<String, Long> getPortfoliosAmount();

    Map<String, Long> getPortfoliosPerFilter(String category);

    List<TechStackDto> findAllTechStacks();

    void increaseViews(Long portfolioId);
}
