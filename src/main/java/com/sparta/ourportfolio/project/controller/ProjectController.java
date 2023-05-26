package com.sparta.ourportfolio.project.controller;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.project.dto.ProjectRequestDto;
import com.sparta.ourportfolio.project.dto.ProjectResponseDto;
import com.sparta.ourportfolio.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", exposedHeaders = "Authorization")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    // 프로젝트 작성 및 파일 업로드
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseDto<String> creatProject(@RequestPart(name = "projectRequestDto") ProjectRequestDto projectRequestDto,
                                            @RequestPart(name = "images", required = false) List<MultipartFile> images,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return projectService.creatProject(projectRequestDto, images, userDetails.getUser());
    }

    // 프로젝트 전체 조회
    @GetMapping
    public ResponseDto<List<ProjectResponseDto>> getProjects() {
        return projectService.getProjects();
    }

    // 프로젝트 상세 조회
    @GetMapping("/{project-id}")
    public ResponseDto<ProjectResponseDto> getProject(@PathVariable(name = "project-id") Long id) {
        return projectService.getProject(id);
    }

    // 프로젝트 수정
    @PatchMapping("/{project-id}")
    public ResponseDto<String> updateProject(@PathVariable(name = "project-id") Long id,
                                     @RequestPart(name = "projectRequestDto") ProjectRequestDto projectRequestDto,
                                     @RequestPart(name = "images", required = false) List<MultipartFile> images,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return projectService.updateProject(id, projectRequestDto, images, userDetails.getUser());
    }

    // 프로젝트 삭제
    @DeleteMapping("/{project-id}")
    public ResponseDto<String> deleteProject(@PathVariable(name = "project-id") Long id,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return projectService.deleteProject(id, userDetails.getUser());
    }

}