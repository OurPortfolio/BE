package com.sparta.ourportfolio.project.controller;

import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.project.dto.ProjectRequestDto;
import com.sparta.ourportfolio.project.dto.ResponseDto;
import com.sparta.ourportfolio.project.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", exposedHeaders = "Authorization")
@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;


    // 프로젝트 작성 및 파일 업로드
    @PostMapping("/api/projects")
    public ResponseDto creatProject(ProjectRequestDto projectRequestDto,
                                    @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return projectService.creatProject(projectRequestDto, images, userDetails.getUser());
    }

    // 프로젝트 전체 조회
    @GetMapping("/api/projects")
    public ResponseDto getProjects() {
        return projectService.getProjects();
    }

    // 프로젝트 상세 조회
    @GetMapping("/api/projects/{project-id}")
    public ResponseDto getProject(@PathVariable(name = "project-id") Long id) {
        return projectService.getProject(id);
    }

    // 프로젝트 수정
    @PatchMapping("/api/projects/{project-id}")
    public ResponseDto updateProject(@PathVariable(name = "project-id") Long id,
                                     ProjectRequestDto projectRequestDto,
                                     @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return projectService.updateProject(id, projectRequestDto, images, userDetails.getUser());
    }

    // 프로젝트 삭제
    @DeleteMapping("/api/projects/{project-id}")
    public ResponseDto deleteProject(@PathVariable(name = "project-id") Long id,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return projectService.deleteProject(id, userDetails.getUser());
    }
}

//    @RequestPart("imgUrl") List<MultipartFile> images
