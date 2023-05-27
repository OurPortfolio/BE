package com.sparta.ourportfolio.portfolio.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.entity.QPortfolio;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.sparta.ourportfolio.portfolio.entity.QPortfolio.portfolio;

public class PortfolioInquiryImpl extends QuerydslRepositorySupport implements PortfolioInquiry {
    @PersistenceContext
    private EntityManager entityManager;

    public PortfolioInquiryImpl() {
        super(Portfolio.class);
    }

    @Override
    public Slice<PortfolioResponseDto> getPortfolios(@Nullable Long lastPortfolioId,
                                                     PageRequest pageRequest,
                                                     @Nullable String category,
                                                     @Nullable String filter) {
        QPortfolio portfolio = QPortfolio.portfolio;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        BooleanBuilder whereBuilder = new BooleanBuilder();
        whereBuilder.and(ltPortfolioId(lastPortfolioId));

        if (category != null && !category.isEmpty()) {
            whereBuilder.and(findByCategory(category));
        }
        if (filter != null && !filter.isEmpty()) {
            whereBuilder.and(findByFilter(filter));
        }

        List<Portfolio> resultSlice = queryFactory
                .select(portfolio)
                .from(portfolio)
                .where(whereBuilder)
                .orderBy(portfolio.id.desc())
                .limit(pageRequest.getPageSize() + 1)
                .fetch();

        List<PortfolioResponseDto> content = resultSlice.stream()
                .map(p -> new PortfolioResponseDto(p, p.getUser()))
                .toList();

        boolean hasNext = false;
        if (content.size() > pageRequest.getPageSize()) {
            resultSlice.remove(pageRequest.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageRequest, hasNext);
    }

    private BooleanExpression findByCategory(String category) {
        return category == null || category.isEmpty() ? null : portfolio.category.eq(category);
    }

    private BooleanExpression findByFilter(String filter) {
        return filter == null || filter.isEmpty() ? null : portfolio.filter.eq(filter);
    }

    @Override
    public Slice<PortfolioResponseDto> searchPortfolios(Long lastPortfolioId,
                                                        PageRequest pageRequest,
                                                        String keyword) {
        QPortfolio portfolio = QPortfolio.portfolio;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        JPQLQuery<Portfolio> query = from(portfolio);
        if (keyword != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            booleanBuilder.or(portfolio.techStack.contains(keyword));
            booleanBuilder.or(portfolio.portfolioTitle.contains(keyword));
            query.where(booleanBuilder, ltPortfolioId(lastPortfolioId));
        } else {
            query.where(ltPortfolioId(lastPortfolioId));
        }

        List<Portfolio> resultSlice = query
                .orderBy(portfolio.id.desc())
                .limit(pageRequest.getPageSize() + 1)
                .fetch();

        List<PortfolioResponseDto> content = resultSlice.stream()
                .map(p -> new PortfolioResponseDto(p, p.getUser()))
                .toList();

        boolean hasNext = false;
        if (content.size() > pageRequest.getPageSize()) {
            resultSlice.remove(pageRequest.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(content, pageRequest, hasNext);
    }

    private BooleanExpression ltPortfolioId(Long id) {
        return id == null ? null : portfolio.id.lt(id);
    }
}


