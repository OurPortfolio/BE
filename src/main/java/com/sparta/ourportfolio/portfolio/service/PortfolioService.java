package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.repository.ProjectRepository;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;

    @Transactional
    public ResponseDto<String> createPortfolio(PortfolioRequestDto portfolioRequestDto, MultipartFile image,
                                               User user) throws IOException {
        User userNow = userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );

        String imageUrl = s3Service.uploadFile(image);
        Portfolio portfolio = new Portfolio(portfolioRequestDto, imageUrl);

        portfolio.setUser(userNow);
        userNow.addPortfolio(portfolio);

        if (!portfolioRequestDto.getProjectIdList().isEmpty()) {
            for (Long projectId : portfolioRequestDto.getProjectIdList()) {
                Project project = projectRepository.findById(projectId).orElseThrow(
                        () -> new IllegalArgumentException("프로젝트가 존재하지 않습니다.")
                );
                portfolio.addProject(project);
                project.setPortfolio(portfolio);
            }
        }
        portfolioRepository.saveAndFlush(portfolio);
        return ResponseDto.setSuccess(HttpStatus.OK, "포트폴리오 생성 완료");
    }

    @Transactional
    public ResponseDto<String> updatePortfolio(Long id, PortfolioRequestDto portfolioRequestDto,
                                               User user) {
        Portfolio portfolio = isExistPortfolio(id);

        User userNow = userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        if (!StringUtils.equals(portfolio.getUser().getId(), userNow.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        portfolio.update(portfolioRequestDto);
        portfolioRepository.save(portfolio);
        return ResponseDto.setSuccess(HttpStatus.OK, "수정 완료");
    }

    @Transactional
    public ResponseDto<String> deletePortfolio(Long id,
                                               User user) {
        Portfolio portfolio = isExistPortfolio(id);

        User userNow = userRepository.findById(user.getId()).orElseThrow(
                () -> new IllegalArgumentException("유저가 존재하지 않습니다.")
        );
        if (!StringUtils.equals(portfolio.getUser().getId(), userNow.getId())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        portfolioRepository.delete(portfolio);
        return ResponseDto.setSuccess(HttpStatus.OK, "삭제 완료");
    }

    //포트폴리오 존재 확인
    public Portfolio isExistPortfolio(Long id) {
        return portfolioRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("포트폴리오가 존재하지 않습니다.")
        );
    }

}
