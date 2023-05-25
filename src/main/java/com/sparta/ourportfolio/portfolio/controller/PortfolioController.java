package com.sparta.ourportfolio.portfolio.controller;

import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioDetailResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.service.PortfolioInquiryService;
import com.sparta.ourportfolio.portfolio.service.PortfolioService;
import com.sparta.ourportfolio.util.ResponseDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final PortfolioInquiryService portfolioInquiryService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<String> createPortfolio(@RequestPart(name = "portfolioRequestDto")
                                                  PortfolioRequestDto portfolioRequestDto,
                                               @RequestPart(name = "portfolioImage") MultipartFile image) throws IOException {
        return portfolioService.createPortfolio(portfolioRequestDto, image);
    }

    @GetMapping("/{portfolio-id}")
    public ResponseDto<PortfolioDetailResponseDto> getPortfolio(@PathVariable(name = "portfolio-id") Long id) {
        return portfolioInquiryService.getPortfolio(id);
    }

    @GetMapping
    public ResponseDto<Slice<PortfolioResponseDto>> getAllPortfolios(@RequestParam(name = "last-portfolio-id") Long id,
                                                                     @RequestParam(name = "size") int size,
                                                                     @Nullable @RequestParam(name = "category") String category,
                                                                     @Nullable @RequestParam(name = "filter") String filter) {
        return portfolioInquiryService.getAllPortfolios(id, size, category, filter);
    }

    @GetMapping("/search")
    public ResponseDto<Slice<PortfolioResponseDto>> searchPortfolios(@RequestParam(name = "keyword") String keyword,
                                                                     @RequestParam(name = "last-portfolio-id") Long id,
                                                                     @RequestParam(name = "size") int size) {
        return portfolioInquiryService.searchPortfolios(keyword, id, size);
    }

    @PatchMapping("/{portfolio-id}")
    public ResponseDto<String> updatePortfolio(@PathVariable(name = "portfolio-id") Long id,
                                                  @RequestBody PortfolioRequestDto portfolioRequestDto) {
        return portfolioService.updatePortfolio(id, portfolioRequestDto);
    }

    @DeleteMapping("/{portfolio-id}")
    public ResponseDto<String> deletePortfolio(@PathVariable(name = "portfolio-id") Long id) {
        return portfolioService.deletePortfolio(id);
    }
}
