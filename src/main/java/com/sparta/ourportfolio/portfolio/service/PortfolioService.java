package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.JacocoGenerated;
import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.dto.TechStackDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.repository.ProjectRepository;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.sparta.ourportfolio.common.exception.ExceptionEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final Trie<String, List<Long>> trie;
    private final Map<String, Integer> searchCountMap = new HashMap<>();

    @PostConstruct
    public void initializeTrieFromRedis() {
        List<TechStackDto> allTechStackData = portfolioRepository.findAllTechStacks();
        trie.clear(); // 기존 Trie 데이터 초기화

        for (TechStackDto data : allTechStackData) {
            Long id = data.getPortfolioId();
            String techStacks = data.getTechStack();
            if (techStacks != null) {
                Arrays.stream(techStacks.split(","))
                        .forEach(techStack -> trie.computeIfAbsent(techStack.trim(), k -> new ArrayList<>()).add(id));
            }
        }
    }

    public void addAutocompleteKeyword(List<String> techStackList, Long id) {
        for (String techStack : techStackList) {
            List<Long> idList = trie.get(techStack);
            if (idList == null) {
                idList = new ArrayList<>();
            }
            idList.add(id);
            trie.put(techStack, idList);
        }
    }

    public void deleteAutocompleteKeyword(List<String> techStackList, Long id) {
        for (String techStack : techStackList) {
            List<Long> idList = trie.get(techStack);
            if (idList != null) {
                boolean removed = idList.remove(id);
                if (removed && idList.isEmpty()) {
                    trie.remove(techStack);
                }
            }
        }
    }

    @JacocoGenerated
    @Transactional(readOnly = true)
    @Cacheable(value = "autocomplete", key = "#keyword")
    public List<String> autoComplete(String keyword) {
        PriorityQueue<Map.Entry<String, Integer>> priorityQueue = new PriorityQueue<>(
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())

        );

        Map<String, Integer> prefixMap = this.trie.prefixMap(keyword)
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> searchCountMap.getOrDefault(e.getKey(), 0)));

        priorityQueue.addAll(prefixMap.entrySet());

        List<String> result = new ArrayList<>(10);
        for (int i = 0; i < 10 && !priorityQueue.isEmpty(); i++) {
            result.add(priorityQueue.poll().getKey());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public ResponseDto<Page<PortfolioResponseDto>> searchPortfolios(String keyword, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "portfolio_id");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PortfolioResponseDto> searchResponseDtoPage =
                portfolioRepository.searchPortfolios(pageable, keyword);
        searchCountMap.put(keyword, searchCountMap.getOrDefault(keyword, 0) + 1);
        return ResponseDto.setSuccess(HttpStatus.OK, "검색 완료", searchResponseDtoPage);
    }

    @Transactional
    public ResponseDto<String> createPortfolio(PortfolioRequestDto portfolioRequestDto,
                                               MultipartFile image,
                                               User user) throws IOException {
        User userNow = userRepository.findById(user.getId()).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER)
        );

        String imageUrl = null;
        if (image != null) {
            imageUrl = s3Service.uploadFile(image);
        }
        Portfolio portfolio = new Portfolio(portfolioRequestDto, imageUrl);

        portfolio.setUser(userNow);
        userNow.addPortfolio(portfolio);

        for (Long projectId : portfolioRequestDto.getProjectIdList()) {
            Project project = isExistProject(projectId);
            if (Objects.equals(project.getUser().getId(), userNow.getId())) {
                portfolio.addProject(project);
                project.setPortfolio(portfolio);
            } else {
                throw new GlobalException(PROJECT_FORBIDDEN);
            }
        }
        portfolioRepository.saveAndFlush(portfolio);

        if (portfolioRequestDto.getTechStack() != null) {
            String techStackData = portfolioRequestDto.getTechStack();
            List<String> techStackList = Arrays.asList(techStackData.split(","));
            addAutocompleteKeyword(techStackList, portfolio.getId());
        }

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
        if (!Objects.equals(portfolio.getUser().getId(), userNow.getId())) {
            throw new GlobalException(UNAUTHORIZED);
        }

        for (Long projectId : portfolioRequestDto.getProjectIdList()) {
            Project project = isExistProject(projectId);
            if (Objects.equals(project.getUser().getId(), userNow.getId())) {
                if (!portfolio.getProjectList().contains(project)) {
                    portfolio.addProject(project);
                    project.setPortfolio(portfolio);
                }
            } else {
                throw new GlobalException(PROJECT_FORBIDDEN);
            }
        }

        String imageUrl = portfolio.getPortfolioImage();
        if (image != null) {
            imageUrl = s3Service.uploadFile(image);
        }

        portfolio.update(portfolioRequestDto, imageUrl);
        return ResponseDto.setSuccess(HttpStatus.OK, "수정 완료");
    }

    @Transactional
    public ResponseDto<String> deletePortfolio(Long id,
                                               User user) {
        Portfolio portfolio = isExistPortfolio(id);

        User userNow = userRepository.findById(user.getId()).orElseThrow(
                () -> new GlobalException(NOT_FOUND_USER)
        );
        if (!Objects.equals(portfolio.getUser().getId(), userNow.getId())) {
            throw new GlobalException(UNAUTHORIZED);
        }
        portfolioRepository.delete(portfolio);

        String techStackData = portfolio.getTechStack();
        List<String> techStackList = Arrays.asList(techStackData.split(","));
        deleteAutocompleteKeyword(techStackList, portfolio.getId());

        return ResponseDto.setSuccess(HttpStatus.OK, "삭제 완료");
    }

    //포트폴리오 존재 확인
    public Portfolio isExistPortfolio(Long id) {
        return portfolioRepository.findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_PORTFOLIO)
        );
    }

    //프로젝트 존재 확인
    private Project isExistProject(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(
                () -> new GlobalException(NOT_FOUND_PROJECT)
        );
    }

}
