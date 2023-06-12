package com.sparta.ourportfolio.portfolio.inquiryService;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.ExceptionEnum;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.portfolio.service.PortfolioInquiryService;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class GetMyPortfolioTest {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private PortfolioInquiryService portfolioInquiryService;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("사용자 Id로 사용자의 포트폴리오 리스트를 조회할 수 있다.")
    @Test
    void getMyPortfolios() {
        //given
        User testUser = createUser(1L, "test@gmail.com", "test-password", "test", false);
        User anonymous = createUser(2L, "anonymous@gmail.com", "test-password", "anonymous", false);
        userRepository.save(testUser);
        userRepository.save(anonymous);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("success","intro",
                "techStack", "residence","location","010********",
                "test@email.com", "coze", "Develop", "Backend","Backend",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("fail","intro",
                "techStack", "residence","location","010********",
                "test@email.com", "coze", "Design", "Graphic","Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("title2","intro",
                "success", "residence","location","010********",
                "test@email.com", "coze", "Photographer", "Wedding","Graphic",
                projectIdList
        );
        String imageUrl = "";

        Portfolio portfolio1 = createPortfolio(1L, portfolioRequestDto1, imageUrl, testUser);
        Portfolio portfolio2 = createPortfolio( 2L, portfolioRequestDto2, imageUrl, testUser);
        Portfolio portfolio3 = createPortfolio( 3L, portfolioRequestDto3, imageUrl, anonymous);
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
        User testUser = createUser(1L, "test@gmail.com", "test-password", "test", false);
        User anonymous = createUser(2L, "anonymous@gmail.com", "test-password", "anonymous", false);
        userRepository.save(testUser);

        //when //then
        assertThatThrownBy(() -> portfolioInquiryService.getMyPortfolios(anonymous))
                .isInstanceOf(GlobalException.class)
                .hasMessage(ExceptionEnum.NOT_FOUND_USER.getMessage());
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