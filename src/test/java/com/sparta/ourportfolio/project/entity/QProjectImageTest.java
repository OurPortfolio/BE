package com.sparta.ourportfolio.project.entity;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.PathInits;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class QProjectImageTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testConstructor_withVariable() {
        // Given
        String variable = "projectImageVariable";

        // When
        QProjectImage qProjectImage = new QProjectImage(variable);

        // Then
        assertNotNull(qProjectImage);
        assertEquals(ProjectImage.class, qProjectImage.getType());
        assertNotNull(qProjectImage.project);
    }

    @Test
    void testConstructor_withPath() {
        // Given
        PathMetadata metadata = PathMetadataFactory.forVariable("path_to_project_image");
        Path<? extends ProjectImage> path = new QProjectImage(metadata);

        // When
        QProjectImage qProjectImage = new QProjectImage(path);

        // Then
        assertNotNull(qProjectImage);
        assertEquals(ProjectImage.class, qProjectImage.getType());
        assertEquals(metadata, qProjectImage.getMetadata());
        assertNotNull(qProjectImage.project);
    }

    @Test
    void testConstructor_withMetadata() {
        // Given
        PathMetadata metadata = PathMetadataFactory.forVariable("path_to_project_image");

        // When
        QProjectImage qProjectImage = new QProjectImage(metadata);

        // Then
        assertNotNull(qProjectImage);
        assertEquals(ProjectImage.class, qProjectImage.getType());
        assertEquals(metadata, qProjectImage.getMetadata());
        assertNotNull(qProjectImage.project);
    }

    @Test
    void testConstructor_withMetadataAndInits() {
        // Given
        PathMetadata metadata = PathMetadataFactory.forVariable("path_to_project_image");
        PathInits inits = new PathInits();

        // When
        QProjectImage qProjectImage = new QProjectImage(metadata, inits);

        // Then
        assertNotNull(qProjectImage);
        assertEquals(ProjectImage.class, qProjectImage.getType());
        assertEquals(metadata, qProjectImage.getMetadata());
    }

}