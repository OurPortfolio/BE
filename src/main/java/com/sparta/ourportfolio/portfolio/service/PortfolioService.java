package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.portfolio.dto.PortfolioDetailResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.util.ResponseDto;
import com.sparta.ourportfolio.util.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final S3Service s3Service;

    @Transactional
    public ResponseDto<String> createPortfolio(PortfolioRequestDto portfolioRequestDto, MultipartFile image) throws IOException {
        String imageUrl = s3Service.uploadFile(image);
        //유저 확인
        Portfolio portfolio = new Portfolio(portfolioRequestDto, imageUrl);
        portfolioRepository.saveAndFlush(portfolio);

        return ResponseDto.setSuccess("포트폴리오 생성 완료");
    }

    @Transactional
    public ResponseDto<String> updatePortfolio(Long id, PortfolioRequestDto portfolioRequestDto) {
        Portfolio portfolio = isExistPortfolio(id);

        //
        portfolio.update(portfolioRequestDto);
        portfolioRepository.save(portfolio);
        return ResponseDto.setSuccess("수정 완료");
    }

    @Transactional
    public ResponseDto<String> deletePortfolio(Long id) {
        Portfolio portfolio = isExistPortfolio(id);

        portfolioRepository.delete(portfolio);
        return ResponseDto.setSuccess("삭제 완료");
    }

    //포트폴리오 존재 확인
    public Portfolio isExistPortfolio(Long id) {
        return portfolioRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다.")
        );
    }


}
