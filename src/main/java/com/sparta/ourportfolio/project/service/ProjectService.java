package com.sparta.ourportfolio.project.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.project.dto.ProjectRequestDto;
import com.sparta.ourportfolio.project.dto.ProjectResponseDto;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.repository.FileRepository;
import com.sparta.ourportfolio.project.repository.ProjectRepository;
import com.sparta.ourportfolio.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.util.List;

import static com.sparta.ourportfolio.common.exception.ExceptionEnum.NOT_FOUND_PROJECT;
import static com.sparta.ourportfolio.common.exception.ExceptionEnum.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;
    private final S3Service s3Service;

    // 프로젝트 작성
    public ResponseDto<ProjectResponseDto> creatProject(ProjectRequestDto projectRequestDto,
                                                        List<MultipartFile> images, User user) throws IOException {

        Project project = new Project(projectRequestDto, user);
        project.setImageFile(s3Service.fileFactory(images, project));
        Project savedProject = projectRepository.save(project);

        return ResponseDto.setSuccess(HttpStatus.OK, "프로젝트 작성 완료", new ProjectResponseDto(savedProject));
    }

    // 프로젝트 상세조회
    @Transactional(readOnly = true)
    public ResponseDto<ProjectResponseDto> getProject(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_PROJECT)
        );
        return ResponseDto.setSuccess(HttpStatus.OK, "상세 조회 성공", new ProjectResponseDto(project));
    }

    // 프로젝트 수정
    public ResponseDto<ProjectResponseDto> updateProject(Long id,
                                                         ProjectRequestDto projectRequestDto,
                                                         List<MultipartFile> images, User user) throws IOException {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_PROJECT)
        );

        //USER 확인
        if (!StringUtils.equals(project.getUser().getId(), user.getId())) {
            throw new GlobalException(UNAUTHORIZED);
        }

        project.setImageFile(s3Service.fileFactory(images, project));
        project.updateProject(projectRequestDto);
        return ResponseDto.setSuccess(HttpStatus.OK, "프로젝트 수정 완료", null);

    }

    // 프로젝트 삭제
    public ResponseDto<ProjectResponseDto> deleteProject(Long id, User user) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new GlobalException(NOT_FOUND_PROJECT)
        );

        //USER 확인
        if (!StringUtils.equals(project.getUser().getId(), user.getId())) {
            throw new GlobalException(UNAUTHORIZED);
        }

        projectRepository.deleteById(id);
        return ResponseDto.setSuccess(HttpStatus.OK, "프로젝트 삭제 완료", null);
    }

}
