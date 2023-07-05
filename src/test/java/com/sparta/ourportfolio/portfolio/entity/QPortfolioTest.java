package com.sparta.ourportfolio.portfolio.entity;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.StringPath;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class QPortfolioTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testConstructor_withVariable() {
        // Given
        String variable = "portfolio";

        // When
        QPortfolio qPortfolio = new QPortfolio(variable);

        // Then
        assertNotNull(qPortfolio);
        assertEquals(Portfolio.class, qPortfolio.getType());
        assertEquals(variable, qPortfolio.getMetadata().getName());
        assertNotNull(qPortfolio.user);
    }

    @Test
    void testConstructor_withMetadata() {
        // Given
        PathMetadata metadata = PathMetadataFactory.forVariable("refreshTokenVariable");

        // When
        QPortfolio qPortfolio = new QPortfolio(metadata);

        // Then
        assertNotNull(qPortfolio);
        assertEquals(Portfolio.class, qPortfolio.getType());
        assertEquals(metadata, qPortfolio.getMetadata());
        assertNotNull(qPortfolio.user);
    }

    @Test
    void testBlogUrl() {
        // Given
        QPortfolio qPortfolio = new QPortfolio("portfolio");

        // When
        StringPath blogUrlPath = qPortfolio.blogUrl;

        // Then
        assertNotNull(blogUrlPath);
        assertEquals("blogUrl", blogUrlPath.getMetadata().getName());
    }

    @Test
    void testCategory() {
        // Given
        QPortfolio qPortfolio = new QPortfolio("portfolio");

        // When
        StringPath categoryPath = qPortfolio.category;

        // Then
        assertNotNull(categoryPath);
        assertEquals("category", categoryPath.getMetadata().getName());
    }

}