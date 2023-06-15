package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.portfolio.dto.PortfolioDetailResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
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

import static com.sparta.ourportfolio.common.exception.ExceptionEnum.NOT_FOUND_PORTFOLIO;

@Service
@RequiredArgsConstructor
public class PortfolioInquiryService {
    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public ResponseDto<PortfolioDetailResponseDto> getPortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_PORTFOLIO)
        );
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
    public ResponseDto<List<PortfolioResponseDto>> getMyPortfolios(User user) {
        List<PortfolioResponseDto> myPortfolioList = portfolioRepository.findAllByUserIdOrderByIdDesc(user.getId())
                .stream()
                .map(portfolio -> new PortfolioResponseDto(portfolio, user))
                .toList();
        return ResponseDto.set(HttpStatus.OK, "MY PORTFOLIO 조회 완료", myPortfolioList);
    }

    @Transactional(readOnly = true)
    public ResponseDto<Long> getLastPortfolioId(String category, String filter) {
        Long lastPortfolioId = portfolioRepository.getLastPortfolioIdByCategoryAndFilter(category, filter) + 1;
        return ResponseDto.setSuccess(HttpStatus.OK, "Last Id 조회 완료", lastPortfolioId);
    }

}
