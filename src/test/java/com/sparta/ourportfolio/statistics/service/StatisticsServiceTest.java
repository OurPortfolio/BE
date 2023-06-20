package com.sparta.ourportfolio.statistics.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.statistics.dto.AllStatisticsDto;
import com.sparta.ourportfolio.statistics.dto.DesignStatisticsDto;
import com.sparta.ourportfolio.statistics.dto.DevelopStatisticsDto;
import com.sparta.ourportfolio.statistics.dto.PhotographerStatisticsDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class StatisticsServiceTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatisticsService statisticsService;

    @AfterEach
    void tearDown() {
        portfolioRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("포트폴리오의 카테고리별 수량을 파악한다.")
    @Test
    void getPortfoliosAmount() {
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
        ResponseDto<AllStatisticsDto> result = statisticsService.getPortfoliosAmount();

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "카테고리별 개수");
        AllStatisticsDto allStatisticsDto = result.getData();
        assertThat(allStatisticsDto)
                .extracting("Develop", "Design", "Photographer")
                .containsExactlyInAnyOrder(1L, 1L, 1L);
    }

    @DisplayName("개발자 포트폴리오의 필터별 수량을 파악한다.")
    @Test
    void getAmountOfDevelop() {
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

        //when
        ResponseDto<DevelopStatisticsDto> result = statisticsService.getAmountOfDevelop();

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "개발자 직무별 포트폴리오");
        DevelopStatisticsDto developStatisticsDto = result.getData();
        assertThat(developStatisticsDto)
                .extracting("backend", "frontend", "ai", "bigdata", "app", "system", "security")
                .containsExactlyInAnyOrder(1L, 1L, 1L, 1L, 1L, 1L, 1L);
    }

    @DisplayName("디자인 포트폴리오의 필터별 수량을 파악한다.")
    @Test
    void getAmountOfDesign() {
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

        //when
        ResponseDto<DesignStatisticsDto> result = statisticsService.getAmountOfDesign();

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "디자이너 직무별 포트폴리오");
        DesignStatisticsDto designStatisticsDto = result.getData();
        assertThat(designStatisticsDto)
                .extracting("Graphic", "uiUx", "Web", "Visual", "Interaction", "Product", "Brand")
                .containsExactlyInAnyOrder(1L, 1L, 1L, 1L, 1L, 1L, 1L);
    }

    @DisplayName("사진작가 포트폴리오의 필터별 수량을 파악한다.")
    @Test
    void getAmountOfPhotographer() {
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

        //when
        ResponseDto<PhotographerStatisticsDto> result = statisticsService.getAmountOfPhotographer();

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "사진작가 직무별 포트폴리오");
        PhotographerStatisticsDto photographerStatisticsDto = result.getData();
        assertThat(photographerStatisticsDto)
                .extracting("commercial", "portrait", "wedding", "fashion", "wildlife", "sports")
                .containsExactlyInAnyOrder(1L, 1L, 1L, 1L, 1L, 1L);
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