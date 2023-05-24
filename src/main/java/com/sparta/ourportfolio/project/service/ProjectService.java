package com.sparta.ourportfolio.project.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.ourportfolio.project.dto.ProjectRequestDto;
import com.sparta.ourportfolio.project.dto.ProjectResponseDto;
import com.sparta.ourportfolio.project.dto.ResponseDto;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.entity.ProjectImage;
import com.sparta.ourportfolio.project.repository.FileRepository;
import com.sparta.ourportfolio.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;
    private static final String S3_BUCKET_PREFIX = "S3";

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    // 프로젝트 작성
    public ResponseDto<?> creatProject(ProjectRequestDto projectRequestDto, List<MultipartFile> images) throws IOException {
        Project project = new Project(projectRequestDto);
        project.setImageFile(fileFactory(images, project));
        project = projectRepository.save(project);

        return ResponseDto.setSuccess("프로젝트 작성 완료", null);
    }

    // 프로젝트 전체조회
    public ResponseDto<List<ProjectResponseDto>> getProjects() {
        List<ProjectResponseDto> projectList = projectRepository.findAll().stream().map(ProjectResponseDto::new).collect(Collectors.toList());
        return ResponseDto.setSuccess("전체 조회 성공", projectList);
    }

    // 프로젝트 상세조회
    public ResponseDto<ProjectResponseDto> getProject(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("프로젝트가 존재하지 않습니다")
        );

        return ResponseDto.setSuccess("상세 조회 성공", new ProjectResponseDto(project));
    }

    // 프로젝트 수정
    public ResponseDto<?> updateProject(Long id,
                                        ProjectRequestDto projectRequestDto,
                                        List<MultipartFile> images) throws IOException {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("프로젝트가 존재하지 않습니다")
        );
        fileRepository.deleteByProjectId(id); // 해당되는 전체 이미지 삭제
        project.setImageFile(fileFactory(images, project));
        project.updateProject(projectRequestDto);
        return ResponseDto.setSuccess("프로젝트 수정 완료.", project);

        //USER 확인부분 추가해야함

    }

    // 프로젝트 삭제
    public ResponseDto<?> deleteProject(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("프로젝트가 존재하지 않습니다")
        );

        //USER 확인부분 추가해야함

        projectRepository.deleteById(id);
        return ResponseDto.setSuccess("프로젝트 삭제를 완료했습니다.", null);
    }

    // 파일 등록 팩토리
    public List<ProjectImage> fileFactory(List<MultipartFile> images, Project project) throws IOException {
        List<ProjectImage> projectImageList = new ArrayList<>();

        for (MultipartFile image : images) {
            // 파일명 새로 부여를 위한 현재 시간 알아내기
            LocalDateTime now = LocalDateTime.now();
            int hour = now.getHour();
            int minute = now.getMinute();
            int second = now.getSecond();
            int millis = now.get(ChronoField.MILLI_OF_SECOND);

            String imageUrl = null;
            String newFileName = "image" + hour + minute + second + millis;
            String fileExtension = '.' + image.getOriginalFilename().replaceAll("^.*\\.(.*)$", "$1");
            String imageName = S3_BUCKET_PREFIX + newFileName + fileExtension;

            // 메타데이터 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(image.getContentType());
            objectMetadata.setContentLength(image.getSize());

            InputStream inputStream = image.getInputStream();

            amazonS3.putObject(new PutObjectRequest(bucketName, imageName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            imageUrl = amazonS3.getUrl(bucketName, imageName).toString();

            projectImageList.add(new ProjectImage(imageUrl, project));
        }
        return projectImageList;
    }

}
