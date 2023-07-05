package com.sparta.ourportfolio.user.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class QUserTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testConstructor_withVariable() {
        // Given
        String variable = "userVariable";

        // When
        QUser qUser = new QUser(variable);

        // Then
        assertNotNull(qUser);
        assertEquals(User.class, qUser.getType());
        assertEquals(variable, qUser.getMetadata().getName());
        assertNotNull(qUser.createdAt);
        assertNotNull(qUser.modifiedAt);
    }

    @Test
    void testConstructor_withPath() {
        // Given
        PathMetadata metadata = PathMetadataFactory.forVariable("path_to_user");
        Path<? extends User> path = new QUser(metadata);

        // When
        QUser qUser = new QUser(path);

        // Then
        assertNotNull(qUser);
        assertEquals(User.class, qUser.getType());
        assertEquals(metadata, qUser.getMetadata());
        assertNotNull(qUser.createdAt);
        assertNotNull(qUser.modifiedAt);
    }

    @Test
    void testConstructor_withMetadata() {
        // Given
        PathMetadata metadata = PathMetadataFactory.forVariable("path_to_user");

        // When
        QUser qUser = new QUser(metadata);

        // Then
        assertNotNull(qUser);
        assertEquals(User.class, qUser.getType());
        assertEquals(metadata, qUser.getMetadata());
        assertNotNull(qUser.createdAt);
        assertNotNull(qUser.modifiedAt);
    }

}