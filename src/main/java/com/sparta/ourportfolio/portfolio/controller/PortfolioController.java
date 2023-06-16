package com.sparta.ourportfolio.portfolio.controller;

import com.sparta.ourportfolio.JacocoGenerated;
import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.portfolio.dto.PortfolioDetailResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.service.PortfolioInquiryService;
import com.sparta.ourportfolio.portfolio.service.PortfolioService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "https://ppol.pro", exposedHeaders = "Authorization")
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
        return portfolioService.createPortfolio(portfolioRequestDto, image, userDetails.user());
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

    @GetMapping("/id")
    public ResponseDto<Long> getLastPortfolioId(@RequestParam(name = "category") String category,
                                                @RequestParam(name = "filter") String filter) {
        return portfolioInquiryService.getLastPortfolioId(category, filter);
    }

    @GetMapping("/search")
    public ResponseDto<Page<PortfolioResponseDto>> searchPortfolios(@RequestParam(name = "keyword") String keyword,
                                                                    @RequestParam(name = "page") int page,
                                                                    @RequestParam(name = "size") int size) {
        return portfolioService.searchPortfolios(keyword, page - 1, size);
    }

    @GetMapping("/myportfolios")
    public ResponseDto<List<PortfolioResponseDto>> getMyPortfolios(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return portfolioInquiryService.getMyPortfolios(userDetails.user());
    }

    @PatchMapping(value = "/{portfolio-id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<String> updatePortfolio(@PathVariable(name = "portfolio-id") Long id,
                                               @RequestPart(name = "portfolioRequestDto")
                                               PortfolioRequestDto portfolioRequestDto,
                                               @RequestPart(name = "portfolioImage") MultipartFile image,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        String techStackData = portfolioRequestDto.getTechStack();
        List<String> techStackList = Arrays.asList(techStackData.split(","));
        portfolioService.addAutocompleteKeyword(techStackList, id);

        return portfolioService.updatePortfolio(id, portfolioRequestDto, image, userDetails.user());
    }

    @DeleteMapping("/{portfolio-id}")
    public ResponseDto<String> deletePortfolio(@PathVariable(name = "portfolio-id") Long id,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return portfolioService.deletePortfolio(id, userDetails.user());
    }

    @JacocoGenerated
    @GetMapping("/autocomplete")
    public ResponseDto<List<String>> autocomplete(@RequestParam String keyword) {
        return portfolioService.autoComplete(keyword);
    }
}
