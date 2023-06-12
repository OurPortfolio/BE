package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.ExceptionEnum;
import com.sparta.ourportfolio.common.exception.GlobalException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class DeletePortfolioServiceTest {

    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private S3Service s3Service;

    @DisplayName("사용자가 작성한 포트폴리오라면 삭제할 수 있다.")
    @Test
    void deletePortfolioByWriter() throws IOException {
        //given
        //포트폴리오 생성
        User testUser = createUser(1L, "test@gmail.com",
                "$2a$10$McegJX6C8dwvMP9/178LEOFgRY/3Xe4KKUEHebjz3hep8.oKmflTy",
                "test", false);
        userRepository.save(testUser);
        Project project1 = createProject(testUser);
        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(project1.getId());
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "velog.coze", "Develop", "Backend",
                projectIdList
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg", "Test Image".getBytes());
        String imageUrl = s3Service.uploadFile(imageFile);

        Portfolio portfolio = createPortfolio(1L, portfolioRequestDto, imageUrl, testUser);
        ;
        portfolioRepository.save(portfolio);

        //when
        ResponseDto<String> result = portfolioService.deletePortfolio(1L, testUser);

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "삭제 완료");
    }

    @DisplayName("사용자가 작성하지 않은 포트폴리오는 삭제할 수 없고 예외가 발생한다.")
    @Test
    void deletePortfolioByUnAuthorizeUser() throws IOException {
        //given
        //포트폴리오 생성
        User writeUser = createUser(1L, "yes@gmail.com",
                "$2a$10$McegJX6C8dwvMP9/178LEOFgRY/3Xe4KKUEHebjz3hep8.oKmflTy",
                "yes", false);
        userRepository.save(writeUser);
        User notWriterUser = createUser(2L, "not@gmail.com",
                "$2a$10$C4bmIc7E5TMAB77CnGObNetLMdxb751tCqp1oX/E093L6G.EuDFhW",
                "not", false);
        userRepository.save(notWriterUser);
        Project project1 = createProject(writeUser);
        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(project1.getId());
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "velog.coze", "Develop", "Backend",
                projectIdList
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg", "Test Image".getBytes());
        String imageUrl = s3Service.uploadFile(imageFile);

        Portfolio portfolio = createPortfolio(2L, portfolioRequestDto, imageUrl, writeUser);
        ;
        portfolioRepository.save(portfolio);

        //when //then
        assertThatThrownBy(() -> portfolioService.deletePortfolio(2L, notWriterUser))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.UNAUTHORIZED.getMessage());
    }

    @DisplayName("존재하지 않는 포트폴리오를 삭제 하려는 경우 예외가 발생한다.")
    @Test
    void deleteNotExistPortfolio() throws IOException {
        //given
        //포트폴리오 생성
        User testUser = createUser(1L, "yes@gmail.com",
                "$2a$10$McegJX6C8dwvMP9/178LEOFgRY/3Xe4KKUEHebjz3hep8.oKmflTy",
                "yes", false);
        userRepository.save(testUser);
        Project project1 = createProject(testUser);
        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(project1.getId());
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "velog.coze", "Develop", "Backend",
                projectIdList
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg", "Test Image".getBytes());
        String imageUrl = s3Service.uploadFile(imageFile);

        Portfolio portfolio = createPortfolio(3L, portfolioRequestDto, imageUrl, testUser);
        ;
        portfolioRepository.save(portfolio);

        //when //then
        assertThatThrownBy(() -> portfolioService.deletePortfolio(3L, testUser))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.NOT_FOUND_PORTFOLIO.getMessage());
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
                                                          String githubId, String blogUrl, String category,
                                                          String filter, String youtubeUrl, List<Long> projectIdList) {
        return PortfolioRequestDto.builder()
                .portfolioTitle(portfolioTitle)
                .intro(intro)
                .techStack(techStack)
                .residence(residence)
                .location(location)
                .telephone(telephone)
                .githubId(githubId)
                .blogUrl(blogUrl)
                .category(category)
                .filter(filter)
                .youtubeUrl(youtubeUrl)
                .projectIdList(projectIdList)
                .build();
    }

    private Portfolio createPortfolio(Long id, PortfolioRequestDto portfolioRequestDto, String image, User user) {
        return Portfolio.builder()
                .id(id)
                .portfolioTitle(portfolioRequestDto.getPortfolioTitle())
                .intro(portfolioRequestDto.getIntro())
                .techStack(portfolioRequestDto.getTechStack())
                .residence(portfolioRequestDto.getResidence())
                .location(portfolioRequestDto.getLocation())
                .telephone(portfolioRequestDto.getTelephone())
                .githubId(portfolioRequestDto.getGithubId())
                .blogUrl(portfolioRequestDto.getBlogUrl())
                .category(portfolioRequestDto.getCategory())
                .filter(portfolioRequestDto.getFilter())
                .youtubeUrl(portfolioRequestDto.getYoutubeUrl())
                .portfolioImage(image)
                .user(user)
                .build();
    }


}