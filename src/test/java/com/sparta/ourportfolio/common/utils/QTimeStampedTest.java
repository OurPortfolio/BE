package com.sparta.ourportfolio.common.utils;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.core.types.dsl.PathInits;
import com.sparta.ourportfolio.common.jwt.refreshtoken.QRefreshToken;
import com.sparta.ourportfolio.common.jwt.refreshtoken.RefreshToken;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class QTimeStampedTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testConstructor_withVariable() {
        // Given
        String variable = "timeStamped";

        // When
        QTimeStamped qTimeStamped = new QTimeStamped(variable);

        // Then
        assertNotNull(qTimeStamped);
        assertEquals(TimeStamped.class, qTimeStamped.getType());
        assertEquals(variable, qTimeStamped.getMetadata().getName());
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
        QTimeStamped qTimeStamped = new QTimeStamped(metadata);

        // Then
        assertNotNull(qTimeStamped);
        assertEquals(TimeStamped.class, qTimeStamped.getType());
        assertEquals(metadata, qTimeStamped.getMetadata());
    }

    @Test
    void testCreatedAt() {
        // Given
        QTimeStamped qTimeStamped = new QTimeStamped("timeStamped");

        // When
        DateTimePath<LocalDateTime> createdAtPath = qTimeStamped.createdAt;

        // Then
        assertNotNull(createdAtPath);
        assertEquals("createdAt", createdAtPath.getMetadata().getName());
    }

    @Test
    void testModifiedAt() {
        // Given
        QTimeStamped qTimeStamped = new QTimeStamped("timeStamped");

        // When
        DateTimePath<java.time.LocalDateTime> modifiedAtPath = qTimeStamped.modifiedAt;

        // Then
        assertNotNull(modifiedAtPath);
        assertEquals("modifiedAt", modifiedAtPath.getMetadata().getName());
    }

}