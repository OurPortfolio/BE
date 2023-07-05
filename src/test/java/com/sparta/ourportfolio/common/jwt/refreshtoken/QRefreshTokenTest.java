package com.sparta.ourportfolio.common.jwt.refreshtoken;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.core.types.dsl.PathInits;
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
class QRefreshTokenTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testConstructor_withVariable() {
        // Given
        String variable = "refreshToken1";

        // When
        QRefreshToken qRefreshToken = new QRefreshToken(variable);

        // Then
        assertNotNull(qRefreshToken);
        assertEquals(RefreshToken.class, qRefreshToken.getType());
        assertEquals(variable, qRefreshToken.getMetadata().getName());
    }

    @Test
    void testConstructor_withPath() {
        // Given
        PathBuilderFactory pathBuilderFactory = new PathBuilderFactory();
        PathInits pathInits = new PathInits(String.valueOf(pathBuilderFactory));
        Path<RefreshToken> path = pathBuilderFactory.create(RefreshToken.class);

        // When
        QRefreshToken qRefreshToken = new QRefreshToken(path);

        // Then
        assertNotNull(qRefreshToken);
        assertEquals(RefreshToken.class, qRefreshToken.getType());
        assertEquals(path.getMetadata(), qRefreshToken.getMetadata());
    }

    @Test
    void testConstructor_withMetadata() {
        // Given
        PathMetadata metadata = PathMetadataFactory.forVariable("refreshTokenVariable");

        // When
        QRefreshToken qRefreshToken = new QRefreshToken(metadata);

        // Then
        assertNotNull(qRefreshToken);
        assertEquals(RefreshToken.class, qRefreshToken.getType());
        assertEquals(metadata, qRefreshToken.getMetadata());
    }

    @Test
    void testEmail() {
        // Given
        QRefreshToken qRefreshToken = new QRefreshToken("refreshToken1");

        // When
        StringPath emailPath = qRefreshToken.email;

        // Then
        assertNotNull(emailPath);
        assertEquals("email", emailPath.getMetadata().getName());
    }

    @Test
    void testId() {
        // Given
        QRefreshToken qRefreshToken = new QRefreshToken("refreshToken1");

        // When
        NumberPath<Long> idPath = qRefreshToken.id;

        // Then
        assertNotNull(idPath);
        assertEquals("id", idPath.getMetadata().getName());
    }

    @Test
    void testRefreshToken() {
        // Given
        QRefreshToken qRefreshToken = new QRefreshToken("refreshToken1");

        // When
        StringPath refreshTokenPath = qRefreshToken.refreshToken;

        // Then
        assertNotNull(refreshTokenPath);
        assertEquals("refreshToken", refreshTokenPath.getMetadata().getName());
    }
}