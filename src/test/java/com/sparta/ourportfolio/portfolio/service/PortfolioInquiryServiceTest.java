package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.ExceptionEnum;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.portfolio.dto.PortfolioDetailResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class PortfolioInquiryServiceTest {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private PortfolioInquiryService portfolioInquiryService;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        portfolioRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    //Get One Portfolio Test
    @DisplayName("포트폴리오 Id로 포트폴리오를 조회할 수 있다.")
    @Test
    void getPortfolio() {
        //given
        User testUser = createUser("test@gmail.com", "test");
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "velog.coze", "Develop", "Backend",
                projectIdList, 0
        );

        String imageUrl = "";

        Portfolio portfolio = createPortfolio(portfolioRequestDto, imageUrl, testUser);
        portfolioRepository.save(portfolio);

        //when
        ResponseDto<PortfolioDetailResponseDto> result = portfolioInquiryService.getPortfolio(portfolio.getId());

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "조회 완료");
    }

    @DisplayName("존재하지 않는 포트폴리오를 조회하면 예외가 발생한다.")
    @Test
    void getPortfolioWithNotExistId() throws IOException {
        assertThatThrownBy(() -> portfolioInquiryService.getPortfolio(1L))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.NOT_FOUND_PORTFOLIO.getMessage());
    }

    //Get All Portfolio Test
    @DisplayName("카테고리와 필터를 지정하지 않으면 모든 포트폴리오를 조회한다.")
    @Test
    void getAllPortfolioWithoutCategoryAndFilter() {
        //given
        User testUser = createUser("test@gmail.com", "test");
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PortfolioRequestDto portfolioRequestDto = PortfolioRequestDto.builder()
                    .portfolioTitle("title")
                    .intro("intro")
                    .techStack("techStack")
                    .projectIdList(projectIdList)
                    .build();
            Portfolio portfolio = createPortfolio(portfolioRequestDto, "", testUser);
            portfolioRepository.save(portfolio);
        }
        PortfolioRequestDto portfolioRequestDto = PortfolioRequestDto.builder()
                .portfolioTitle("title")
                .intro("intro")
                .techStack("techStack")
                .projectIdList(projectIdList)
                .build();
        Portfolio portfolio = createPortfolio(portfolioRequestDto, "", testUser);
        portfolioRepository.save(portfolio);
        Long portfolioId = portfolioRepository.save(portfolio).getId();

        //when
        ResponseDto<Slice<PortfolioResponseDto>> result = portfolioInquiryService.getAllPortfolios(
                portfolioId + 1, 9, "", "");
        List<PortfolioResponseDto> content = result.getData().getContent();
        Long lastPortfolioId = content.get(content.size() - 1).getId();

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, String.valueOf(lastPortfolioId));

        Slice<PortfolioResponseDto> responseData = result.getData();
        List<PortfolioResponseDto> portfolioResults = responseData.getContent();
        assertThat(portfolioResults).hasSize(10);
    }

    @DisplayName("카테고리와 필터를 지정하면 해당하는 포트폴리오를 조회한다.")
    @Test
    void getAllPortfolioWithCategoryAndFilter() {
        //given
        User testUser = createUser("test@gmail.com", "test");
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("success", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList, 0
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("fail", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Graphic", "Graphic",
                projectIdList, 0
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("fail", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "None", "Graphic",
                projectIdList, 0
        );
        String imageUrl = "";

        Portfolio portfolio1 = createPortfolio(portfolioRequestDto1, imageUrl, testUser);
        Portfolio portfolio2 = createPortfolio(portfolioRequestDto2, imageUrl, testUser);
        Portfolio portfolio3 = createPortfolio(portfolioRequestDto3, imageUrl, testUser);
        Long portfolioId = portfolioRepository.save(portfolio1).getId();
        portfolioRepository.save(portfolio2);
        portfolioRepository.save(portfolio3);

        //when
        ResponseDto<Slice<PortfolioResponseDto>> result = portfolioInquiryService.getAllPortfolios(
                portfolioId + 1, 9, "Develop", "Backend");
        List<PortfolioResponseDto> content = result.getData().getContent();
        Long lastPortfolioId = content.get(content.size() - 1).getId();

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, String.valueOf(lastPortfolioId));

        Slice<PortfolioResponseDto> responseData = result.getData();
        List<PortfolioResponseDto> portfolioResults = responseData.getContent();
        assertThat(portfolioResults).hasSize(1);

        PortfolioResponseDto expectedPortfolio = portfolioResults.get(0);
        assertThat(expectedPortfolio.getPortfolioTitle()).isEqualTo("success");
    }

    //Get My Portfolio Test
    @DisplayName("사용자 Id로 사용자의 포트폴리오 리스트를 조회할 수 있다.")
    @Test
    void getMyPortfolios() {
        //given
        User testUser = createUser("test@gmail.com", "test");
        User anonymous = createUser("anonymous@gmail.com", "anonymous");
        userRepository.save(testUser);
        userRepository.save(anonymous);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("success", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList, 0
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("fail", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Graphic", "Graphic",
                projectIdList, 0
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("title2", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "None", "Graphic",
                projectIdList, 0
        );
        String imageUrl = "";

        Portfolio portfolio1 = createPortfolio(portfolioRequestDto1, imageUrl, testUser);
        Portfolio portfolio2 = createPortfolio(portfolioRequestDto2, imageUrl, testUser);
        Portfolio portfolio3 = createPortfolio(portfolioRequestDto3, imageUrl, anonymous);
        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);
        portfolioRepository.save(portfolio3);

        //when
        ResponseDto<List<PortfolioResponseDto>> result = portfolioInquiryService.getMyPortfolios(testUser);

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "MY PORTFOLIO 조회 완료");

        List<PortfolioResponseDto> portfolioResults = result.getData();
        assertThat(portfolioResults).hasSize(2);
    }

    //Get Portfolio Id Test
    @DisplayName("카테고리와 필터를 지정하면 해당하는 포트폴리오 중 최신 포트폴리오 Id + 1된 Id를 조회할 수 있다.")
    @Test
    void getLastPortfolioIdWithCategoryAndFilter() {
        //given
        User testUser = createUser("test@gmail.com", "test");
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("title1", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList, 0
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("title2", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Graphic", "Graphic",
                projectIdList, 0
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("title2", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "Wedding", "Graphic",
                projectIdList, 0
        );
        String imageUrl = "";

        Portfolio portfolio1 = createPortfolio(portfolioRequestDto1, imageUrl, testUser);
        Portfolio portfolio2 = createPortfolio(portfolioRequestDto2, imageUrl, testUser);
        Portfolio portfolio3 = createPortfolio(portfolioRequestDto3, imageUrl, testUser);
        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);
        Long portfolioId = portfolioRepository.save(portfolio3).getId();

        //when
        ResponseDto<Long> result = portfolioInquiryService.getLastPortfolioId("Photographer", "Wedding");

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "Last Id 조회 완료");

        Long resultId = result.getData();
        assertThat(resultId).isEqualTo(portfolioId + 1);
    }

    @DisplayName("포트폴리오를 조회수가 높은 순서대로 12개 조회한다.")
    @Test
    void getPortfoliosByViews() {
        //given
        User testUser = createUser("test@gmail.com", "test");
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PortfolioRequestDto requestDto1 = createPortfolioRequestDto("title1", "intro1",
                    "techStack1", "residence1", "location1", "010********",
                    "test1@email.com", "coze1", "Develop1", "Backend1", "Backend1",
                    projectIdList, i
            );
            Portfolio portfolio = createPortfolio(requestDto1, "", testUser);
            portfolioRepository.save(portfolio);
        }

        //when
        ResponseDto<List<PortfolioResponseDto>> result = portfolioInquiryService.getPortfoliosByViews();

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "지금 뜨는 포트폴리오");
        assertThat(result.getData().size()).isEqualTo(12);
    }

    private User createUser(String email, String nickname) {
        return User.builder()
                .email(email)
                .password("test-password")
                .nickname(nickname)
                .isDeleted(false)
                .build();
    }

    private PortfolioRequestDto createPortfolioRequestDto(String portfolioTitle, String intro, String techStack,
                                                          String residence, String location, String telephone,
                                                          String githubId, String blogUrl, String category,
                                                          String filter, String youtubeUrl, List<Long> projectIdList,
                                                          long views) {
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
                .views(views)
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
                .views(portfolioRequestDto.getViews())
                .build();
    }


}