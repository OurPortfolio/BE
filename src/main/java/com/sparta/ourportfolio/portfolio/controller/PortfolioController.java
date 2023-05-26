package com.sparta.ourportfolio.portfolio.controller;

import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioDetailResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.dto.SearchResponseDto;
import com.sparta.ourportfolio.portfolio.service.PortfolioInquiryService;
import com.sparta.ourportfolio.portfolio.service.PortfolioService;
import com.sparta.ourportfolio.common.dto.ResponseDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", exposedHeaders = "Authorization")
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final PortfolioInquiryService portfolioInquiryService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<String> createPortfolio(@RequestPart(name = "portfolioRequestDto")
                                                  PortfolioRequestDto portfolioRequestDto,
                                               @RequestPart(name = "portfolioImage") MultipartFile image,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return portfolioService.createPortfolio(portfolioRequestDto, image, userDetails.getUser());
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
    public ResponseDto<Slice<SearchResponseDto>> searchPortfolios(@RequestParam(name = "keyword") String keyword,
                                                                  @RequestParam(name = "last-portfolio-id") Long id,
                                                                  @RequestParam(name = "size") int size) {
        return portfolioInquiryService.searchPortfolios(keyword, id, size);
    }

    @GetMapping("/myportfolios")
    public ResponseDto<List<PortfolioResponseDto>> getMyPortfolios(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return portfolioInquiryService.getMyPortfolios(userDetails.getUser());
    }

    @PatchMapping("/{portfolio-id}")
    public ResponseDto<String> updatePortfolio(@PathVariable(name = "portfolio-id") Long id,
                                               @RequestBody PortfolioRequestDto portfolioRequestDto,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return portfolioService.updatePortfolio(id, portfolioRequestDto, userDetails.getUser());
    }

    @DeleteMapping("/{portfolio-id}")
    public ResponseDto<String> deletePortfolio(@PathVariable(name = "portfolio-id") Long id,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return portfolioService.deletePortfolio(id, userDetails.getUser());
    }
}
