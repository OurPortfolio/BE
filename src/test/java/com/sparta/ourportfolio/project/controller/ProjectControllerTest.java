package com.sparta.ourportfolio.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.security.UserDetailsImpl;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.project.dto.ProjectRequestDto;
import com.sparta.ourportfolio.project.dto.ProjectResponseDto;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.repository.FileRepository;
import com.sparta.ourportfolio.project.repository.ProjectRepository;
import com.sparta.ourportfolio.project.service.ProjectService;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private S3Service s3Service;

    @AfterEach
    void tearDown() {
        fileRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("프로젝트 생성")
    @Test
    void createProject() throws Exception {
        // given
        User user1 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);

        UserDetailsImpl userDetails1 = new UserDetailsImpl(userRepository.findById(user1.getId()).get());

        ProjectRequestDto projectRequestDto1 = createProjectRequestDto("1", "2", "3", "4", "5");
        String createJson = objectMapper.writeValueAsString(projectRequestDto1);
        MockMultipartFile requestDto1 = new MockMultipartFile("projectRequestDto", "projectRequestDto1", "application/json", createJson.getBytes(StandardCharsets.UTF_8));

        // 이미지 파일을 생성하여 리스트에 추가
        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile imageFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "Test Image".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "Test Image".getBytes());
        images.add(imageFile1);
        images.add(imageFile2);

        ResponseDto<ProjectResponseDto> projectResponse = projectService.creatProject(projectRequestDto1, images, user1);

        // when // then
        mockMvc.perform(
                        multipart("/api/projects")
                                .file(requestDto1)
                                .file("images", imageFile1.getBytes())
                                .file("images", imageFile2.getBytes())
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user(userDetails1))
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("프로젝트 상세 조회")
    @Test
    void getProject() throws Exception {
        // given
        User user1 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);

        UserDetailsImpl userDetails1 = new UserDetailsImpl(userRepository.findById(user1.getId()).get());

        ProjectRequestDto projectRequestDto1 = createProjectRequestDto("1", "2", "3", "4", "5");
        String createJson = objectMapper.writeValueAsString(projectRequestDto1);
        MockMultipartFile requestDto1 = new MockMultipartFile("projectRequestDto", "projectRequestDto1", "application/json", createJson.getBytes(StandardCharsets.UTF_8));

        // 이미지 파일을 생성하여 리스트에 추가
        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile imageFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "Test Image".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "Test Image".getBytes());
        images.add(imageFile1);
        images.add(imageFile2);

        Project project = new Project(projectRequestDto1, user1);
        project.setImageFile(s3Service.fileFactory(images, project));
        project = projectRepository.save(project);

        ResponseDto<ProjectResponseDto> projectResponse = projectService.creatProject(projectRequestDto1, images, user1);

        // when // then
        mockMvc.perform(
                        multipart(HttpMethod.GET, "/api/projects/3")
                                .file(requestDto1)
                                .file("images", imageFile1.getBytes())
                                .file("images", imageFile2.getBytes())
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(user(userDetails1))
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("프로젝트 수정 완료")
    @Test
    void updateProject() throws Exception {
        // given
        User user1 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);

        UserDetailsImpl userDetails1 = new UserDetailsImpl(userRepository.findById(1L).get());

        ProjectRequestDto projectRequestDto1 = createProjectRequestDto("1", "2", "3", "4", "5");

        // 이미지 파일을 생성하여 리스트에 추가
        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile imageFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "Test Image".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "Test Image".getBytes());
        images.add(imageFile1);
        images.add(imageFile2);

        Project project = new Project(projectRequestDto1, user1);
        project.setImageFile(s3Service.fileFactory(images, project));
        project = projectRepository.save(project);

        ProjectRequestDto newProjectRequestDto = createProjectRequestDto("5", "4", "3", "2", "1");
        String newCreateJson = objectMapper.writeValueAsString(newProjectRequestDto);
        MockMultipartFile newRequestDto = new MockMultipartFile("projectRequestDto", "newProjectRequestDto", "application/json", newCreateJson.getBytes(StandardCharsets.UTF_8));

        List<MultipartFile> newImages = new ArrayList<>();
        MockMultipartFile imageFile3 = new MockMultipartFile("image3", "test3.jpg", "image/jpeg", "Test Image".getBytes());
        MockMultipartFile imageFile4 = new MockMultipartFile("image4", "test4.jpg", "image/jpeg", "Test Image".getBytes());
        newImages.add(imageFile3);
        newImages.add(imageFile4);

        // when // then
        mockMvc.perform(
                        multipart(HttpMethod.PATCH, "/api/projects/1")
                                .file(newRequestDto)
                                .file("images", imageFile3.getBytes())
                                .file("images", imageFile4.getBytes())
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(user(userDetails1))
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @DisplayName("프로젝트 삭제 완료")
    @Test
    void deleteProject() throws Exception {
        // given
        User user1 = createUser("test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);

        UserDetailsImpl userDetails1 = new UserDetailsImpl(userRepository.findById(user1.getId()).get());

        ProjectRequestDto projectRequestDto1 = createProjectRequestDto("1", "2", "3", "4", "5");
        String createJson1 = objectMapper.writeValueAsString(projectRequestDto1);
        MockMultipartFile requestDto1 = new MockMultipartFile("projectRequestDto", "projectRequestDto1", "application/json", createJson1.getBytes(StandardCharsets.UTF_8));

        // 이미지 파일을 생성하여 리스트에 추가
        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile imageFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "Test Image".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "Test Image".getBytes());
        images.add(imageFile1);
        images.add(imageFile2);

        Project project = new Project(projectRequestDto1, user1);
        project.setImageFile(s3Service.fileFactory(images, project));
        project = projectRepository.save(project);

        // when // then
        mockMvc.perform(
                        multipart(HttpMethod.DELETE, "/api/projects/2")
                                .with(user(userDetails1))
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    private User createUser(String email, String password, String nickname, boolean isDeleted) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .isDeleted(isDeleted)
                .build();
    }

    private ProjectRequestDto createProjectRequestDto(String title, String term, String people, String position, String description) {
        return ProjectRequestDto.builder()
                .title(title)
                .term(term)
                .people(people)
                .position(position)
                .description(description)
                .build();
    }

}