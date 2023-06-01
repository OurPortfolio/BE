package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.repository.ProjectRepository;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.sparta.ourportfolio.common.exception.ExceptionEnum.*;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final RedisTemplate<String, String> redisTemplate;
    private final Trie trie;

    @PostConstruct
    public void initializeTrieFromRedis() {
        List<String> autocompleteData = redisTemplate.opsForList().range("autocomplete", 0, -1);
        trie.clear(); // 기존 Trie 데이터 초기화
        for (String data : autocompleteData) {
            trie.put(data, null);
        }
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<String>> autoComplete(String keyword) {
        List<String> result = (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                .collect(Collectors.toList());
        return ResponseDto.setSuccess(HttpStatus.OK, "검색어 자동완성 완료", result);
    }

    public void addAutocompleteKeyword(List<String> techStackList) {
        for (String techStack : techStackList) {
            this.trie.put(techStack, null);
            redisTemplate.opsForList().rightPush("autocomplete", techStack);
        }
    }

    public void deleteAutocompleteKeyword(List<String> techStackList) {
        for (String techStack : techStackList) {
            this.trie.remove(techStack);
            redisTemplate.opsForList().remove("autocomplete", 0, techStack);
        }
    }

    @Transactional
    public ResponseDto<String> createPortfolio(PortfolioRequestDto portfolioRequestDto,
                                               MultipartFile image,
                                               User user) throws IOException {
        User userNow = userRepository.findById(user.getId()).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER)
        );

        String imageUrl = null;
        if (!image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image);
        }
        Portfolio portfolio = new Portfolio(portfolioRequestDto, imageUrl);

        portfolio.setUser(userNow);
        userNow.addPortfolio(portfolio);

        if (portfolioRequestDto.getProjectIdList() == null) {
            throw new GlobalException(PORTFOLIO_ID_LIST_IS_NULL);
        }
        for (Long projectId : portfolioRequestDto.getProjectIdList()) {
            Project project = projectRepository.findById(projectId).orElseThrow(
                    () -> new GlobalException(NOT_FOUND_PROJECT)
            );
            if (StringUtils.equals(project.getUser().getId(), userNow.getId())) {
                portfolio.addProject(project);
                project.setPortfolio(portfolio);
            } else {
                throw new GlobalException(PROJECT_FORBIDDEN);
            }
        }

        portfolioRepository.saveAndFlush(portfolio);

        return ResponseDto.setSuccess(HttpStatus.OK, "포트폴리오 생성 완료");
    }

    @Transactional
    public ResponseDto<String> updatePortfolio(Long id,
                                               PortfolioRequestDto portfolioRequestDto,
                                               MultipartFile image,
                                               User user) throws IOException {
        Portfolio portfolio = isExistPortfolio(id);

        User userNow = userRepository.findById(user.getId()).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER)
        );
        if (!StringUtils.equals(portfolio.getUser().getId(), userNow.getId())) {
            throw new GlobalException(UNAUTHORIZED);
        }

        if (portfolioRequestDto.getProjectIdList() == null) {
            throw new GlobalException(PORTFOLIO_ID_LIST_IS_NULL);
        }
        for (Long projectId : portfolioRequestDto.getProjectIdList()) {
            Project project = projectRepository.findById(projectId).orElseThrow(
                    () -> new GlobalException(NOT_FOUND_PROJECT)
            );
            if (!portfolio.getProjectList().contains(project) &&
                    StringUtils.equals(project.getUser().getId(), userNow.getId())) {
                portfolio.addProject(project);
                project.setPortfolio(portfolio);
            } else {
                throw new GlobalException(PROJECT_FORBIDDEN);
            }
        }

        String imageUrl = null;
        if (!image.isEmpty()) {
            imageUrl = s3Service.uploadFile(image);
        }

        portfolio.update(portfolioRequestDto, imageUrl);
        portfolioRepository.save(portfolio);
        return ResponseDto.setSuccess(HttpStatus.OK, "수정 완료");
    }

    @Transactional
    public ResponseDto<String> deletePortfolio(Long id,
                                               User user) {
        Portfolio portfolio = isExistPortfolio(id);

        User userNow = userRepository.findById(user.getId()).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER)
        );
        if (!StringUtils.equals(portfolio.getUser().getId(), userNow.getId())) {
            throw new GlobalException(UNAUTHORIZED);
        }

        String techStackData = portfolio.getTechStack();
        List<String> techStackList = Arrays.asList(techStackData.split(","));
        deleteAutocompleteKeyword(techStackList);

        portfolioRepository.delete(portfolio);
        return ResponseDto.setSuccess(HttpStatus.OK, "삭제 완료");
    }

    //포트폴리오 존재 확인
    public Portfolio isExistPortfolio(Long id) {
        return portfolioRepository.findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_PORTFOLIO)
        );
    }


}
