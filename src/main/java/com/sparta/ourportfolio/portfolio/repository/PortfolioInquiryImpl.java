package com.sparta.ourportfolio.portfolio.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.dto.TechStackDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.entity.QPortfolio;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sparta.ourportfolio.portfolio.entity.QPortfolio.portfolio;
import static com.sparta.ourportfolio.user.entity.QUser.user;

public class PortfolioInquiryImpl extends QuerydslRepositorySupport implements PortfolioInquiry {
    @PersistenceContext
    private EntityManager entityManager;

    public PortfolioInquiryImpl() {
        super(Portfolio.class);
    }

    @Override
    public List<TechStackDto> findAllTechStacks() {
        QPortfolio portfolio = QPortfolio.portfolio;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        List<Portfolio> findResult = queryFactory
                .select(portfolio)
                .from(portfolio)
                .join(portfolio.user, user)
                .fetchJoin()
                .fetch();

        return findResult.stream().map(TechStackDto::new).toList();
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
                .join(portfolio.user, user)
                .fetchJoin()
                .where(whereBuilder)
                .orderBy(portfolio.id.desc())
                .limit((long) pageRequest.getPageSize() + 1)
                .fetch();

        List<PortfolioResponseDto> content = resultSlice.stream()
                .map(p -> new PortfolioResponseDto(p, p.getUser()))
                .toList();
        boolean hasNext = content.size() >= pageRequest.getPageSize();

        return new SliceImpl<>(content, pageRequest, hasNext);
    }

    @Override
    public Page<PortfolioResponseDto> searchPortfolios(Pageable pageable,
                                                       String keyword) {
        QPortfolio portfolio = QPortfolio.portfolio;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        BooleanBuilder whereBuilder = buildKeywordCondition(keyword);

        List<Portfolio> result = queryFactory
                .select(portfolio)
                .from(portfolio)
                .join(portfolio.user, user)
                .fetchJoin()
                .where(whereBuilder)
                .orderBy(portfolio.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(portfolio.count())
                .from(portfolio)
                .where(whereBuilder);

        List<PortfolioResponseDto> content = result.stream()
                .map(p -> new PortfolioResponseDto(p, p.getUser()))
                .toList();

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    public Long getLastPortfolioIdByCategoryAndFilter(String category, String filter) {
        QPortfolio portfolio = QPortfolio.portfolio;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        BooleanBuilder whereBuilder = new BooleanBuilder();
        if (category != null && !category.isEmpty()) {
            whereBuilder.and(findByCategory(category));
        }
        if (filter != null && !filter.isEmpty()) {
            whereBuilder.and(findByFilter(filter));
        }

        Long lastPortfolioId = queryFactory.select(portfolio.id)
                .from(portfolio)
                .where(whereBuilder)
                .orderBy(portfolio.id.desc())
                .fetchFirst();
        return lastPortfolioId != null ? lastPortfolioId : -1L;
    }

    public Map<String, Long> getPortfoliosAmount() {
        QPortfolio portfolio = QPortfolio.portfolio;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        JPAQuery<Tuple> countQuery = queryFactory
                .select(
                        portfolio.category,
                        portfolio.count()
                )
                .from(portfolio)
                .groupBy(portfolio.category);

        List<Tuple> result = countQuery.fetch();

        Map<String, Long> resultMap = new HashMap<>();
        for (Tuple tuple : result) {
            resultMap.put(tuple.get(portfolio.category), tuple.get(portfolio.count()));
        }

        return resultMap;
    }

    public Map<String, Long> getPortfoliosPerFilter(String category) {
        QPortfolio portfolio = QPortfolio.portfolio;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        BooleanBuilder whereBuilder = new BooleanBuilder();
        whereBuilder.and(findByCategory(category));

        JPAQuery<Tuple> countQuery = queryFactory
                .select(
                        portfolio.filter,
                        portfolio.count()
                )
                .from(portfolio)
                .where(whereBuilder)
                .groupBy(portfolio.filter);
        List<Tuple> result = countQuery.fetch();

        Map<String, Long> resultMap = new HashMap<>();
        for (Tuple tuple : result) {
            resultMap.put(tuple.get(portfolio.filter), tuple.get(portfolio.count()));
        }

        return resultMap;
    }

    private BooleanExpression ltPortfolioId(Long id) {
        return id == null ? null : portfolio.id.lt(id);
    }

    private BooleanExpression findByCategory(String category) {
        return category == null || category.isEmpty() ? null : portfolio.category.eq(category);
    }

    private BooleanExpression findByFilter(String filter) {
        return filter == null || filter.isEmpty() ? null : portfolio.filter.eq(filter);
    }

    private BooleanBuilder buildKeywordCondition(String keyword) {
        BooleanBuilder whereBuilder = new BooleanBuilder();
        if (keyword != null && !keyword.isEmpty()) {
            whereBuilder.or(findByKeywordInTechStack(keyword));
            whereBuilder.or(findByKeywordInPortfolioTitle(keyword));
        }
        return whereBuilder;
    }

    private BooleanExpression findByKeywordInTechStack(String keyword) {
        return keyword == null || keyword.isEmpty() ? null : portfolio.techStack.contains(keyword);
    }

    private BooleanExpression findByKeywordInPortfolioTitle(String keyword) {
        return keyword == null || keyword.isEmpty() ? null : portfolio.portfolioTitle.contains(keyword);
    }


}


