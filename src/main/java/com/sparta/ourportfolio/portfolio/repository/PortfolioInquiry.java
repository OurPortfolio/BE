package com.sparta.ourportfolio.portfolio.repository;

import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.dto.SearchResponseDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

public interface PortfolioInquiry {
    Slice<PortfolioResponseDto> getPortfolios(Long lastPortfolioId, PageRequest pageRequest, String category,
                                              String filter);
    Slice<SearchResponseDto> searchPortfolios(Long lastPortfolioId, PageRequest pageRequest, String keyword);
}
