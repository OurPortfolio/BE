package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.project.dto.ProjectRequestDto;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.repository.ProjectRepository;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CreatePortfolioServiceTest {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private ProjectRepository projectRepository;

    @DisplayName("포트폴리오를 생성한다.")
    @Test
    void createPortfolio() throws IOException {
        //given
        User testUser = createUser(1L, "test@gmail.com",
                "$2a$10$McegJX6C8dwvMP9/178LEOFgRY/3Xe4KKUEHebjz3hep8.oKmflTy",
                "test", false);
        userRepository.save(testUser);

        Project project1 = createProject(testUser);
        Project project2 = createProject(testUser);

        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(project1.getId());
        projectIdList.add(project2.getId());
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title","intro",
                "techStack", "residence","location","010********",
                "test@email.com", "coze", "velog.coze", "Develop","Backend",
                "youtube",projectIdList
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg", "Test Image".getBytes());
        String imageUrl = s3Service.uploadFile(imageFile);

        Portfolio portfolio = new Portfolio(portfolioRequestDto, imageUrl);
        portfolioRepository.save(portfolio);

        //when
        ResponseDto<String> result = portfolioService.createPortfolio(portfolioRequestDto, imageFile, testUser);

        //then
        assertThat(result)
                .extracting("statusCode","message")
                .contains(HttpStatus.OK, "포트폴리오 생성 완료");
    }


    private Project createProject(User testUser) throws IOException {
        ProjectRequestDto projectRequestDto1 = createProjectRequestDto(
                "1", "2", "3", "4", "5"
        );

        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "Test Image".getBytes()
        );
        images.add(imageFile);

        Project project = new Project(projectRequestDto1, testUser);
        project.setImageFile(s3Service.fileFactory(images, project));
        project = projectRepository.save(project);
        return project;
    }

    private User createUser(Long id, String email, String password, String nickname, boolean isDeleted) {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .nickname(nickname)
                .isDeleted(isDeleted)
                .build();
    }

    private ProjectRequestDto createProjectRequestDto(String title, String term, String people, String position, String description) {
        return ProjectRequestDto.builder()
                .title(title)
                .term(term)
                .people(people)
                .position(position)
                .description(description)
                .build();
    }

    private PortfolioRequestDto createPortfolioRequestDto(String portfolioTitle, String intro, String techStack,
                                                          String residence, String location, String telephone,
                                                          String email, String githubId, String blogUrl, String category,
                                                          String filter, String youtubeUrl, List<Long> projectIdList) {
        return PortfolioRequestDto.builder()
                .portfolioTitle(portfolioTitle)
                .intro(intro)
                .techStack(techStack)
                .residence(residence)
                .location(location)
                .telephone(telephone)
                .email(email)
                .githubId(githubId)
                .blogUrl(blogUrl)
                .category(category)
                .filter(filter)
                .youtubeUrl(youtubeUrl)
                .projectIdList(projectIdList)
                .build();
    }

}