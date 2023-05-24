package com.sparta.ourportfolio.portfolio.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
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
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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
                                                     @Nullable String filter,
                                                     @Nullable String search) {
        QPortfolio portfolio = QPortfolio.portfolio;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        BooleanBuilder whereBuilder = new BooleanBuilder();
        whereBuilder.and(ltPortfolioId(lastPortfolioId));
        if (search != null && !search.isEmpty()) {
            whereBuilder.and(findBySearch(search));
        }
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
                .map(PortfolioResponseDto::new)
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

    private BooleanExpression findBySearch(String search) {
        return search == null || search.isEmpty() ? null : portfolio.portfolioTitle.containsIgnoreCase(search);
    }

    private BooleanExpression ltPortfolioId(Long id) {
        return id == null ? null : portfolio.id.lt(id);
    }
}


