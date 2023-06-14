package com.sparta.ourportfolio.portfolio.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.ExceptionEnum;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.portfolio.dto.PortfolioDetailResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.portfolio.service.PortfolioInquiryService;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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
    void getPortfolio() throws IOException {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto = createPortfolioRequestDto("title", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "velog.coze", "Develop", "Backend",
                projectIdList
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
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
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
                portfolioId+1, 9, "", "");

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "조회 완료");

        Slice<PortfolioResponseDto> responseData = result.getData();
        List<PortfolioResponseDto> portfolioResults = responseData.getContent();
        assertThat(portfolioResults).hasSize(9);
    }

    @DisplayName("카테고리와 필터를 지정하면 해당하는 포트폴리오를 조회한다.")
    @Test
    void getAllPortfolioWithCategoryAndFilter() {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("success", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("fail", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Graphic", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("fail", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "None", "Graphic",
                projectIdList
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
                portfolioId+1, 9, "Develop", "Backend");

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "조회 완료");

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
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        User anonymous = createUser("anonymous@gmail.com", "test-password", "anonymous", false);
        userRepository.save(testUser);
        userRepository.save(anonymous);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("success", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("fail", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Graphic", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("title2", "intro",
                "success", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "None", "Graphic",
                projectIdList
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

    @DisplayName("존재하지 않는 사용자 Id로 조회할 경우 예외가 발생한다.")
    @Test
    void getMyPortfoliosWithNotExistUser() {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        User anonymous = User.builder()
                .id(200L)
                .build();
        userRepository.save(testUser);

        //when //then
        assertThatThrownBy(() -> portfolioInquiryService.getMyPortfolios(anonymous))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.NOT_FOUND_USER.getMessage());
    }

    //Search Portfolio Test
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
        ResponseDto<Page<PortfolioResponseDto>> result = portfolioInquiryService.searchPortfolios("success", 0, 100);

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

    //Get Portfolio Id Test
    @DisplayName("카테고리와 필터를 지정하면 해당하는 포트폴리오 중 최신 포트폴리오 Id + 1된 Id를 조회할 수 있다.")
    @Test
    void getLastPortfolioIdWithCategoryAndFilter() {
        //given
        User testUser = createUser("test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("title1", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("title2", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Graphic", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("title2", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "Wedding", "Graphic",
                projectIdList
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
        assertThat(resultId).isEqualTo(portfolioId+1);
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