package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioDetailResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.dto.SearchResponseDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioInquiryService {
    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public ResponseDto<PortfolioDetailResponseDto> getPortfolio(Long id) {
        Portfolio portfolio = isExistPortfolio(id);

        PortfolioDetailResponseDto portfolioDetailResponseDto = new PortfolioDetailResponseDto(portfolio);
        return ResponseDto.setSuccess(HttpStatus.OK, "조회 완료", portfolioDetailResponseDto);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Slice<PortfolioResponseDto>> getAllPortfolios(Long id,
                                                                     int size,
                                                                     String category,
                                                                     String filter) {
        PageRequest pageRequest = PageRequest.of(0, size);

        Slice<PortfolioResponseDto> portfolioResponseDtoSlice =
                portfolioRepository.getPortfolios(id, pageRequest, category, filter);
        return ResponseDto.setSuccess(HttpStatus.OK, "조회 완료", portfolioResponseDtoSlice);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Slice<SearchResponseDto>> searchPortfolios(String keyword,
                                                                  Long id,
                                                                  int size) {
        PageRequest pageRequest = PageRequest.of(0, size);

        Slice<SearchResponseDto> searchResponseDtoSlice =
                portfolioRepository.searchPortfolios(id, pageRequest, keyword);
        return ResponseDto.setSuccess(HttpStatus.OK, "검색 완료", searchResponseDtoSlice);
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<PortfolioResponseDto>> getMyPortfolios(User user) {
        List<PortfolioResponseDto> myPortfolioList = portfolioRepository.findAllByUser_IdOrderByIdDesc(user.getId())
                .stream().map(PortfolioResponseDto::new).toList();
        return ResponseDto.set(HttpStatus.OK, "MY PORTFOLIO 조회 완료", myPortfolioList);
    }

    public Portfolio isExistPortfolio(Long id) {
        return portfolioRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다.")
        );
    }

}
