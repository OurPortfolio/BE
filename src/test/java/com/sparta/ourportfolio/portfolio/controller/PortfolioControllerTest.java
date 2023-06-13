package com.sparta.ourportfolio.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.portfolio.service.PortfolioService;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;

    @AfterEach
    void tearDown() {
        portfolioRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("포트폴리오를 작성한다.")
    @Test
    void createPortfolio() throws Exception {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        UserDetailsImpl userDetails = new UserDetailsImpl(userRepository.findByNickname("test").get());

        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto requestDto = createPortfolioRequestDto("title1", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList
        );
        String createJson = objectMapper.writeValueAsString(requestDto);

        MockMultipartFile portfolioRequestDto = new MockMultipartFile(
                "portfolioRequestDto", "portfolioRequestDto", "application/json", createJson.getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image1", "test1.jpg", "image/jpeg", "Test Image".getBytes()
        );

        portfolioService.createPortfolio(requestDto, imageFile, testUser);

        //when //then
        mockMvc.perform(
                        multipart("/api/portfolios")
                                .file(portfolioRequestDto)
                                .file("portfolioImage", imageFile.getBytes())
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(user(userDetails))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("포트폴리오 Id로 포트폴리오를 조회한다.")
    @Test
    void getPortfolio() throws Exception {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto requestDto = createPortfolioRequestDto("title1", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image1", "test1.jpg", "image/jpeg", "Test Image".getBytes()
        );

        // 포트폴리오 생성 및 저장
        Portfolio portfolio = createPortfolio(requestDto, "", testUser);
        Long portfolioId = portfolioRepository.save(portfolio).getId();

        //when //then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/portfolios/" + portfolioId) // get 요청에 따른 엔드포인트 수정
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("카테고리와 필터에 해당하는 포트폴리오를 조회 한다.")
    @Test
    void getPortfoliosWithCategoryAndFilter() throws Exception {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto requestDto1 = createPortfolioRequestDto("title1", "intro1",
                "techStack1", "residence1", "location1", "010********",
                "test1@email.com", "coze1", "Develop1", "Backend1", "Backend1",
                projectIdList
        );
        PortfolioRequestDto requestDto2 = createPortfolioRequestDto("title2", "intro2",
                "techStack2", "residence2", "location2", "010********",
                "test2@email.com", "coze2", "Develop2", "Backend2", "Backend2",
                projectIdList
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image1", "test1.jpg", "image/jpeg", "Test Image".getBytes()
        );

        // 포트폴리오 생성 및 저장
        portfolioService.createPortfolio(requestDto1, imageFile, testUser);
        portfolioService.createPortfolio(requestDto2, imageFile, testUser);

        //when //then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/portfolios")
                                .param("last-portfolio-id", "10")
                                .param("size", "10")
                                .param("category", "Develop1")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("카테고리와 필터에 해당하는 마지막 포트폴리오 Id를 조회 한다.")
    @Test
    void getLastPortfolioId() throws Exception {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto requestDto1 = createPortfolioRequestDto("title1", "intro1",
                "techStack1", "residence1", "location1", "010********",
                "test1@email.com", "coze1", "Develop1", "Backend1", "Backend1",
                projectIdList
        );
        PortfolioRequestDto requestDto2 = createPortfolioRequestDto("title2", "intro2",
                "techStack2", "residence2", "location2", "010********",
                "test2@email.com", "coze2", "Develop2", "Backend2", "Backend2",
                projectIdList
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image1", "test1.jpg", "image/jpeg", "Test Image".getBytes()
        );

        // 포트폴리오 생성 및 저장
        portfolioService.createPortfolio(requestDto1, imageFile, testUser);
        portfolioService.createPortfolio(requestDto2, imageFile, testUser);
        ;

        //when //then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/portfolios/id")
                                .param("category", "Develop1")
                                .param("filter", "Backend1")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("키워드를 입력하면 해당하는 포트폴리오들을 조회 한다.")
    @Test
    void searchPortfolios() throws Exception {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto requestDto1 = createPortfolioRequestDto("title1", "intro1",
                "techStack1", "residence1", "location1", "010********",
                "test1@email.com", "coze1", "Develop1", "Backend1", "Backend1",
                projectIdList
        );
        PortfolioRequestDto requestDto2 = createPortfolioRequestDto("title2", "intro2",
                "techStack2", "residence2", "location2", "010********",
                "test2@email.com", "coze2", "Develop2", "Backend2", "Backend2",
                projectIdList
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image1", "test1.jpg", "image/jpeg", "Test Image".getBytes()
        );

        // 포트폴리오 생성 및 저장
        portfolioService.createPortfolio(requestDto1, imageFile, testUser);
        portfolioService.createPortfolio(requestDto2, imageFile, testUser);
        ;

        //when //then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/portfolios/search")
                                .param("keyword", "title1")
                                .param("page", "1")
                                .param("size", "20")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("로그인된 유저가 작성한 포트폴리오들을 조회 한다.")
    @Test
    void getMyPortfolios() throws Exception {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        UserDetailsImpl userDetails = new UserDetailsImpl(userRepository.findByNickname("test").get());
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto requestDto1 = createPortfolioRequestDto("title1", "intro1",
                "techStack1", "residence1", "location1", "010********",
                "test1@email.com", "coze1", "Develop1", "Backend1", "Backend1",
                projectIdList
        );
        PortfolioRequestDto requestDto2 = createPortfolioRequestDto("title2", "intro2",
                "techStack2", "residence2", "location2", "010********",
                "test2@email.com", "coze2", "Develop2", "Backend2", "Backend2",
                projectIdList
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image1", "test1.jpg", "image/jpeg", "Test Image".getBytes()
        );

        // 포트폴리오 생성 및 저장
        portfolioService.createPortfolio(requestDto1, imageFile, testUser);
        portfolioService.createPortfolio(requestDto2, imageFile, testUser);
        ;

        //when //then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/portfolios/myportfolios")
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("포트폴리오를 수정한다.")
    @Test
    void updatePortfolio() throws Exception {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        UserDetailsImpl userDetails = new UserDetailsImpl(userRepository.findByNickname("test").get());
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto requestDto = createPortfolioRequestDto("title1", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList
        );
        Portfolio portfolio = createPortfolio(requestDto, "", testUser);
        Long portfolioId = portfolioRepository.save(portfolio).getId();

        PortfolioRequestDto updateDto = createPortfolioRequestDto("modified title", "intro",
                "techStack2", "residence2", "location2", "010********",
                "test@email.com", "coze", "Develop2", "Backend2", "Backend",
                projectIdList
        );
        String createJson = objectMapper.writeValueAsString(updateDto);
        MockMultipartFile updateRequestDto = new MockMultipartFile(
                "portfolioRequestDto", "portfolioRequestDto", "application/json", createJson.getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile imageFile = new MockMultipartFile(
                "image1", "test1.jpg", "image/jpeg", "Test Image".getBytes()
        );

        //when //then
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/api/portfolios/" + portfolioId)
                                .file(updateRequestDto)
                                .file("portfolioImage", imageFile.getBytes())
                                .contentType(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(user(userDetails))
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("포트폴리오를 삭제한다.")
    @Test
    void deletePortfolio() throws Exception {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        UserDetailsImpl userDetails = new UserDetailsImpl(userRepository.findByNickname("test").get());
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto requestDto = createPortfolioRequestDto("title1", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList
        );
        Portfolio portfolio = createPortfolio(requestDto, "", testUser);
        Long portfolioId = portfolioRepository.save(portfolio).getId();

        //when //then
        mockMvc.perform(
                        delete("/api/portfolios/" + portfolioId)
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    //추후 진행
//    @DisplayName("키워드를 techStack 검색어로 자동 완성 한다.")
//    @Test
//    void autoComplete() {
//        //given
//
//
//        //when //then
//    }


    private User createUser(String email, String password, String nickname, boolean isDeleted) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .isDeleted(isDeleted)
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