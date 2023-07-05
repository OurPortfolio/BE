package com.sparta.ourportfolio.project.entity;

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
class QProjectTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testConstructor_withVariable() {
        // Given
        String variable = "projectVariable";

        // When
        QProject qProject = new QProject(variable);

        // Then
        assertNotNull(qProject);
        assertEquals(Project.class, qProject.getType());
        assertNotNull(qProject.portfolio);
        assertNotNull(qProject.user);
    }

    @Test
    void testConstructor_withMetadata() {
        // Given
        PathMetadata metadata = PathMetadataFactory.forVariable("path_to_project");

        // When
        QProject qProject = new QProject(metadata);

        // Then
        assertNotNull(qProject);
        assertEquals(Project.class, qProject.getType());
        assertEquals(metadata, qProject.getMetadata());
        assertNotNull(qProject.portfolio);
        assertNotNull(qProject.user);
    }

    @Test
    void testConstructor_withMetadataAndInits() {
        // Given
        PathMetadata metadata = PathMetadataFactory.forVariable("path_to_project");
        PathInits inits = new PathInits();

        // When
        QProject qProject = new QProject(metadata, inits);

        // Then
        assertNotNull(qProject);
        assertEquals(Project.class, qProject.getType());
        assertEquals(metadata, qProject.getMetadata());
    }

}