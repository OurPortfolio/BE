package com.sparta.ourportfolio.common.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.entity.ProjectImage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;
    private static final String S3_BUCKET_PREFIX = "S3";

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 파일 등록 팩토리
    public List<ProjectImage> fileFactory(List<MultipartFile> images, Project project) throws IOException {
        List<ProjectImage> projectImageList = new ArrayList<>();

        images = images.stream()
                .map(s -> s.isEmpty() ? null : s)
                .collect(Collectors.toList());

        for (MultipartFile image : images) {
            if (image == null) break;

            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String imageUrl = null;

            // 메타데이터 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(image.getContentType());
            objectMetadata.setContentLength(image.getSize());

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            imageUrl = amazonS3.getUrl(bucketName, fileName).toString();

            projectImageList.add(new ProjectImage(imageUrl, project));
        }
        return projectImageList;
    }

    //파일을 s3에 업로드
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        isFileExists(multipartFile);

        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    //파일 유 / 무 확인 메서드
    private void isFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new NullPointerException("파일이 존재하지 않습니다.");
        }
    }
}


//    public boolean delete(String fileUrl) {
//        try {
//            String[] temp = fileUrl.split("/");
//            String fileKey = temp[temp.length - 1];
//            amazonS3.deleteObject(bucketName, fileKey);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
