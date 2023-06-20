package com.sparta.ourportfolio.statistics.controller;

import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @AfterEach
    void tearDown() {
        portfolioRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("포트폴리오의 카테고리별 수량을 파악한다.")
    @Test
    void getPortfoliosAmount() throws Exception {
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

        //when //then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/statistics") // get 요청에 따른 엔드포인트 수정
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("개발자 포트폴리오의 필터별 수량을 파악한다.")
    @Test
    void getAmountOfDevelop() throws Exception {
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
                "test@email.com", "coze", "Develop", "Frontend", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "AI", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto4 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Big Data", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto5 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "App", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto6 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "System", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto7 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Security", "Graphic",
                projectIdList
        );

        String imageUrl = "";

        Portfolio portfolio1 = createPortfolio(portfolioRequestDto1, "title search success", testUser);
        Portfolio portfolio2 = createPortfolio(portfolioRequestDto2, imageUrl, testUser);
        Portfolio portfolio3 = createPortfolio(portfolioRequestDto3, imageUrl, testUser);
        Portfolio portfolio4 = createPortfolio(portfolioRequestDto4, imageUrl, testUser);
        Portfolio portfolio5 = createPortfolio(portfolioRequestDto5, imageUrl, testUser);
        Portfolio portfolio6 = createPortfolio(portfolioRequestDto6, imageUrl, testUser);
        Portfolio portfolio7 = createPortfolio(portfolioRequestDto7, imageUrl, testUser);
        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);
        portfolioRepository.save(portfolio3);
        portfolioRepository.save(portfolio4);
        portfolioRepository.save(portfolio5);
        portfolioRepository.save(portfolio6);
        portfolioRepository.save(portfolio7);

        //when //then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/statistics/develop") // get 요청에 따른 엔드포인트 수정
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("디자인 포트폴리오의 필터별 수량을 파악한다.")
    @Test
    void getAmountOfDesign() throws Exception {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("success", "intro",
                "", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Graphic", "Backend",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("fail", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "UI/UX", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Web", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto4 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Visual", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto5 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Interaction", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto6 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Product", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto7 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Brand", "Graphic",
                projectIdList
        );

        String imageUrl = "";

        Portfolio portfolio1 = createPortfolio(portfolioRequestDto1, "title search success", testUser);
        Portfolio portfolio2 = createPortfolio(portfolioRequestDto2, imageUrl, testUser);
        Portfolio portfolio3 = createPortfolio(portfolioRequestDto3, imageUrl, testUser);
        Portfolio portfolio4 = createPortfolio(portfolioRequestDto4, imageUrl, testUser);
        Portfolio portfolio5 = createPortfolio(portfolioRequestDto5, imageUrl, testUser);
        Portfolio portfolio6 = createPortfolio(portfolioRequestDto6, imageUrl, testUser);
        Portfolio portfolio7 = createPortfolio(portfolioRequestDto7, imageUrl, testUser);
        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);
        portfolioRepository.save(portfolio3);
        portfolioRepository.save(portfolio4);
        portfolioRepository.save(portfolio5);
        portfolioRepository.save(portfolio6);
        portfolioRepository.save(portfolio7);

        //when //then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/statistics/design") // get 요청에 따른 엔드포인트 수정
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("사진작가 포트폴리오의 필터별 수량을 파악한다.")
    @Test
    void getAmountOfPhotographer() throws Exception {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("success", "intro",
                "", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "Commercial", "Backend",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("fail", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "Portrait", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "Wedding", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto4 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "Fashion", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto5 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "Wildlife", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto6 = createPortfolioRequestDto("stack search success", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "Sports", "Graphic",
                projectIdList
        );

        String imageUrl = "";

        Portfolio portfolio1 = createPortfolio(portfolioRequestDto1, "title search success", testUser);
        Portfolio portfolio2 = createPortfolio(portfolioRequestDto2, imageUrl, testUser);
        Portfolio portfolio3 = createPortfolio(portfolioRequestDto3, imageUrl, testUser);
        Portfolio portfolio4 = createPortfolio(portfolioRequestDto4, imageUrl, testUser);
        Portfolio portfolio5 = createPortfolio(portfolioRequestDto5, imageUrl, testUser);
        Portfolio portfolio6 = createPortfolio(portfolioRequestDto6, imageUrl, testUser);
        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);
        portfolioRepository.save(portfolio3);
        portfolioRepository.save(portfolio4);
        portfolioRepository.save(portfolio5);
        portfolioRepository.save(portfolio6);

        //when //then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/statistics/photographer") // get 요청에 따른 엔드포인트 수정
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

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