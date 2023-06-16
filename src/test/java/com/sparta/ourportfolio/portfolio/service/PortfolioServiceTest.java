package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.ExceptionEnum;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.project.dto.ProjectRequestDto;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.repository.FileRepository;
import com.sparta.ourportfolio.project.repository.ProjectRepository;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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
class PortfolioServiceTest {

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
    @Autowired
    private FileRepository fileRepository;

    @AfterEach
    void tearDown() {
        fileRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        portfolioRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("포트폴리오의 제목과 기술 스택에 키워드가 해당하는 포트폴리오들을 조회할 수 있다.")
    @Test
    void searchPortfolios() {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("success", "intro",
                "", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("fail", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Graphic", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "None", "Graphic",
                projectIdList
        );
        String imageUrl = "";

        Portfolio portfolio1 = createPortfolio(portfolioRequestDto1, "title search success", testUser);
        Portfolio portfolio2 = createPortfolio(portfolioRequestDto2, imageUrl, testUser);
        Portfolio portfolio3 = createPortfolio(portfolioRequestDto3, imageUrl, testUser);
        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);
        portfolioRepository.save(portfolio3);

        //when
        ResponseDto<Page<PortfolioResponseDto>> result = portfolioService.searchPortfolios("success", 0, 100);

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "검색 완료");

        Page<PortfolioResponseDto> responseData = result.getData();
        List<PortfolioResponseDto> portfolioResults = responseData.getContent();
        assertThat(responseData).hasSize(2);

        assertThat(portfolioResults.get(0).getPortfolioTitle()).isEqualTo("stack search success");
        assertThat(portfolioResults.get(1).getPortfolioImage()).isEqualTo("title search success");
    }

    //Create Test
    @DisplayName("포트폴리오를 생성한다.")
    @Test
    void createPortfolio() throws IOException {
        //given
        User testUser = createUser("test@gmail.com", "test password", "test", false);
        userRepository.save(testUser);

        Project project1 = createProject(testUser);
        Project project2 = createProject(testUser);

        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(project1.getId());
        projectIdList.add(project2.getId());
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


        //when
        ResponseDto<String> result = portfolioService.createPortfolio(portfolioRequestDto, imageFile, testUser);

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "포트폴리오 생성 완료");
    }

    @DisplayName("포트폴리오 생성 시 존재하지 않는 프로젝트를 추가하면 예외가 발생한다.")
    @Test
    void createPortfolioWithNotExistProject() throws IOException {
        //given
        User testUser = createUser("test@gmail.com", "test password", "test", false);
        userRepository.save(testUser);

        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(1L);
        projectIdList.add(2L);
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

        //when //then
        assertThatThrownBy(() -> portfolioService.createPortfolio(portfolioRequestDto, imageFile, testUser))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.NOT_FOUND_PROJECT.getMessage());
    }

    @DisplayName("포트폴리오 생성 시 본인의 프로젝트가 아닌 프로젝트를 추가하면 예외가 발생한다.")
    @Test
    void createPortfolioWithUnAuthorizedProject() throws IOException {
        //given
        User testUser = createUser("test@gmail.com", "test password", "test", false);
        userRepository.save(testUser);
        User anonymous = createUser("anonymous@gmail.com", "test password", "anonymous", false);
        userRepository.save(anonymous);

        Project project1 = createProject(anonymous);
        Project project2 = createProject(anonymous);
        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(project1.getId());
        projectIdList.add(project2.getId());
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

        //when //then
        assertThatThrownBy(() -> portfolioService.createPortfolio(portfolioRequestDto, imageFile, testUser))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.PROJECT_FORBIDDEN.getMessage());
    }

    @DisplayName("존재하지 않는 유저가 포트폴리오를 추가하면 예외가 발생한다.")
    @Test
    void createPortfolioWithAnonymousUser() {
        //given
        User anonymous = User.builder()
                .id(10L)
                .build();
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "velog.coze", "Develop", "Backend",
                projectIdList
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg", "Test Image".getBytes());

        //when //then
        assertThatThrownBy(() -> portfolioService.createPortfolio(portfolioRequestDto, imageFile, anonymous))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.NOT_FOUND_USER.getMessage());
    }

    //Update Test
    @DisplayName("기존 포트폴리오의 내용과 이미지를 수정할 수 있다.")
    @Test
    void updatePortfolio() throws IOException {
        //given
        User testUser = createUser("test@gmail.com", "test password", "test", false);
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

        Portfolio portfolio = createPortfolio(portfolioRequestDto, imageUrl, testUser);

        portfolioRepository.save(portfolio);

        //수정 데이터 준비
        Project project2 = createProject(testUser);
        List<Long> updateProjectIdList = new ArrayList<>();
        updateProjectIdList.add(project1.getId());
        updateProjectIdList.add(project2.getId());
        PortfolioRequestDto updatePortfolioRequestDto = createPortfolioRequestDto("updateTitle", "upIntro",
                "upTechStack", "upResidence", "upLocation", "01055489692",
                "update@email.com", "updateId", "updateBlog", "Develop", "Backend",
                updateProjectIdList
        );
        MockMultipartFile updateImageFile = new MockMultipartFile(
                "image",
                "update-test.jpg",
                "image/jpeg", "Test Image".getBytes());
        String updateImageUrl = s3Service.uploadFile(imageFile);

        portfolio.update(updatePortfolioRequestDto, updateImageUrl);

        //when
        ResponseDto<String> result = portfolioService.updatePortfolio(
                portfolio.getId(), updatePortfolioRequestDto, updateImageFile, testUser
        );

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "수정 완료");
    }

    @DisplayName("사용자가 작성하지 않은 포트폴리오는 수정할 수 없고 예외가 발생한다.")
    @Test
    void updatePortfolioByUnAuthorizeUser() throws IOException {
        //given
        //포트폴리오 생성
        User testUser = createUser("test@gmail.com", "test password", "test", false);
        userRepository.save(testUser);
        Project project3 = createProject(testUser);
        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(project3.getId());
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

        Portfolio portfolio = createPortfolio(portfolioRequestDto, imageUrl, testUser);

        Long portfolioId = portfolioRepository.save(portfolio).getId();

        //수정 데이터 준비
        User anonymous = createUser("anonymous@gmail.com", "test password", "anonymous", false);
        userRepository.save(anonymous);
        Project project4 = createProject(testUser);
        List<Long> updateProjectIdList = new ArrayList<>();
        updateProjectIdList.add(project4.getId());
        PortfolioRequestDto updatePortfolioRequestDto = createPortfolioRequestDto("updateTitle", "upIntro",
                "upTechStack", "upResidence", "upLocation", "01055489692",
                "update@email.com", "updateId", "updateBlog", "Develop", "Backend",
                updateProjectIdList
        );
        MockMultipartFile updateImageFile = new MockMultipartFile(
                "image",
                "update-test.jpg",
                "image/jpeg", "Test Image".getBytes());
        String updateImageUrl = s3Service.uploadFile(imageFile);

        portfolio.update(updatePortfolioRequestDto, updateImageUrl);

        //when //then
        assertThatThrownBy(() -> portfolioService.updatePortfolio(
                portfolioId, updatePortfolioRequestDto, updateImageFile, anonymous))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.UNAUTHORIZED.getMessage());
    }

    @DisplayName("포트폴리오는 수정 시 존재하지 않는 프로젝트를 추가 하면 예외가 발생한다.")
    @Test
    void updatePortfolioWithNotExistProject() throws IOException {
        //given
        //포트폴리오 생성
        User testUser = createUser("test@gmail.com", "test password", "test", false);
        userRepository.save(testUser);
        Project project5 = createProject(testUser);
        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(project5.getId());
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

        Portfolio portfolio = createPortfolio(portfolioRequestDto, imageUrl, testUser);

        Long portfolioId = portfolioRepository.save(portfolio).getId();

        //수정 데이터 준비
        List<Long> updateProjectIdList = new ArrayList<>();
        updateProjectIdList.add(6L);
        updateProjectIdList.add(7L);
        PortfolioRequestDto updatePortfolioRequestDto = createPortfolioRequestDto("updateTitle", "upIntro",
                "upTechStack", "upResidence", "upLocation", "01055489692",
                "update@email.com", "updateId", "updateBlog", "Develop", "Backend",
                updateProjectIdList
        );
        MockMultipartFile updateImageFile = new MockMultipartFile(
                "image",
                "update-test.jpg",
                "image/jpeg", "Test Image".getBytes());
        String updateImageUrl = s3Service.uploadFile(imageFile);

        portfolio.update(updatePortfolioRequestDto, updateImageUrl);

        //when //then
        assertThatThrownBy(() -> portfolioService.updatePortfolio(
                portfolioId, updatePortfolioRequestDto, updateImageFile, testUser))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.NOT_FOUND_PROJECT.getMessage());
    }

    @DisplayName("포트폴리오 수정 시 사용자가 작성하지 않은 프로젝트를 추가 하면 예외가 발생한다.")
    @Test
    void updatePortfolioWithUnAuthorizedProject() throws IOException {
        //given
        //포트폴리오 생성
        User testUser = createUser("test@gmail.com", "test password", "test", false);
        userRepository.save(testUser);
        Project project6 = createProject(testUser);
        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(project6.getId());
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

        Portfolio portfolio = createPortfolio(portfolioRequestDto, imageUrl, testUser);

        Long portfolioId = portfolioRepository.save(portfolio).getId();

        //수정 데이터 준비
        User anonymous = createUser("anonymous@gmail.com", "test password", "anonymous", false);
        userRepository.save(anonymous);
        Project project7 = createProject(anonymous);
        List<Long> updateProjectIdList = new ArrayList<>();
        updateProjectIdList.add(project7.getId());
        PortfolioRequestDto updatePortfolioRequestDto = createPortfolioRequestDto("updateTitle", "upIntro",
                "upTechStack", "upResidence", "upLocation", "01055489692",
                "update@email.com", "updateId", "updateBlog", "Develop", "Backend",
                updateProjectIdList
        );
        MockMultipartFile updateImageFile = new MockMultipartFile(
                "image",
                "update-test.jpg",
                "image/jpeg", "Test Image".getBytes());
        String updateImageUrl = s3Service.uploadFile(imageFile);

        portfolio.update(updatePortfolioRequestDto, updateImageUrl);

        //when //then
        assertThatThrownBy(() -> portfolioService.updatePortfolio(
                portfolioId, updatePortfolioRequestDto, updateImageFile, testUser))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.PROJECT_FORBIDDEN.getMessage());
    }

    @DisplayName("존재하지 않는 유저가 포트폴리오 수정 시 예외가 발생한다.")
    @Test
    void updatePortfolioWithAnonymousUser() throws IOException {
        //given
        User testUser = createUser("test@gmail.com", "test password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
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
        Portfolio portfolio = createPortfolio(portfolioRequestDto, imageUrl, testUser);
        Long portfolioId = portfolioRepository.save(portfolio).getId();

        //수정 데이터 준비
        User anonymous = User.builder()
                .id(10L)
                .build();
        List<Long> updateProjectIdList = new ArrayList<>();
        PortfolioRequestDto updatePortfolioRequestDto = createPortfolioRequestDto("updateTitle", "upIntro",
                "upTechStack", "upResidence", "upLocation", "01055489692",
                "update@email.com", "updateId", "updateBlog", "Develop", "Backend",
                updateProjectIdList
        );
        MockMultipartFile updateImageFile = new MockMultipartFile(
                "image",
                "update-test.jpg",
                "image/jpeg", "Test Image".getBytes());
        String updateImageUrl = s3Service.uploadFile(imageFile);

        //when //then
        assertThatThrownBy(() -> portfolioService.updatePortfolio(
                portfolioId, updatePortfolioRequestDto, updateImageFile, anonymous))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.NOT_FOUND_USER.getMessage());
    }

    @DisplayName("Image가 null일 경우 기존 포트폴리오 이미지로 유지한다.")
    @Test
    void updatePortfolioWithoutImage() throws IOException {
        //given
        User testUser = createUser("test@gmail.com", "test password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
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
        Portfolio portfolio = createPortfolio(portfolioRequestDto, imageUrl, testUser);
        portfolioRepository.save(portfolio);

        //수정 데이터 준비
        User anonymous = User.builder()
                .id(10L)
                .build();
        List<Long> updateProjectIdList = new ArrayList<>();
        PortfolioRequestDto updatePortfolioRequestDto = createPortfolioRequestDto("updateTitle", "upIntro",
                "upTechStack", "upResidence", "upLocation", "01055489692",
                "update@email.com", "updateId", "updateBlog", "Develop", "Backend",
                updateProjectIdList
        );
        MockMultipartFile updateImageFile = null;
        String updateImageUrl = portfolio.getPortfolioImage();
        portfolio.update(updatePortfolioRequestDto, updateImageUrl);

        //when
        ResponseDto<String> result = portfolioService.updatePortfolio(
                portfolio.getId(), updatePortfolioRequestDto, updateImageFile, testUser
        );

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "수정 완료");
    }

    //Delete Test
    @DisplayName("사용자가 작성한 포트폴리오라면 삭제할 수 있다.")
    @Test
    void deletePortfolioByWriter() throws IOException {
        //given
        User testUser = createUser("test@gmail.com", "test password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title", "intro",
                "techStack Spring React", "residence", "location", "010********",
                "test@email.com", "coze", "velog.coze", "Develop", "Backend",
                projectIdList
        );
        Portfolio portfolio = createPortfolio(portfolioRequestDto, "imageUrl", testUser);
        portfolioRepository.save(portfolio);

        //when
        ResponseDto<String> result = portfolioService.deletePortfolio(portfolio.getId(), testUser);

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
        User writeUser = createUser("yes@gmail.com", "test password", "yes", false);
        userRepository.save(writeUser);
        User notWriterUser = createUser("not@gmail.com", "test password", "not", false);
        userRepository.save(notWriterUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "velog.coze", "Develop", "Backend",
                projectIdList
        );
        Portfolio portfolio = createPortfolio(portfolioRequestDto, "imageUrl", writeUser);
        portfolioRepository.save(portfolio);

        //when //then
        assertThatThrownBy(() -> portfolioService.deletePortfolio(portfolioId, notWriterUser))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.UNAUTHORIZED.getMessage());
    }

    @DisplayName("존재하지 않는 포트폴리오를 삭제 하려는 경우 예외가 발생한다.")
    @Test
    void deleteNotExistPortfolio() throws IOException {
        //given
        //포트폴리오 생성
        User testUser = createUser("yes@gmail.com", "test password", "yes", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title", "intro",
                "techStack,Spring,React", "residence", "location", "010********",
                "test@email.com", "coze", "velog.coze", "Develop", "Backend",
                projectIdList
        );
        Portfolio portfolio = createPortfolio(portfolioRequestDto, "imageUrl", testUser);
        portfolioRepository.save(portfolio);

        //when //then
        assertThatThrownBy(() -> portfolioService.deletePortfolio(-1L, testUser))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.NOT_FOUND_PORTFOLIO.getMessage());
    }

    @DisplayName("존재하지 않는 유저가 포트폴리오 삭제 시 예외가 발생한다.")
    @Test
    void deletePortfolioWithAnonymousUser() throws IOException {
        //given
        User testUser = createUser("yes@gmail.com", "test password", "yes", false);
        userRepository.save(testUser);
        User anonymous = User.builder()
                .id(10L)
                .build();
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "velog.coze", "Develop", "Backend",
                projectIdList
        );

        Portfolio portfolio = createPortfolio(portfolioRequestDto, "imageUrl", testUser);
        Long portfolioId = portfolioRepository.save(portfolio).getId();

        //when //then
        assertThatThrownBy(() -> portfolioService.deletePortfolio(portfolioId, anonymous))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.NOT_FOUND_USER.getMessage());
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

    private User createUser(String email, String password, String nickname, boolean isDeleted) {
        return User.builder()
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

    private Portfolio createPortfolio(PortfolioRequestDto portfolioRequestDto, String image, User user) {
        return Portfolio.builder()
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