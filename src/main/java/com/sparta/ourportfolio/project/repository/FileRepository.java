package com.sparta.ourportfolio.project.repository;

import com.sparta.ourportfolio.project.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<ProjectImage, Long> {
    void deleteByProjectId(Long projectId);
}
