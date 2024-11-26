package com.example.find_it.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private static final String BUCKET_NAME = "capstonem"; // S3 버킷 이름

    // 파일 업로드 메서드
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // MultipartFile을 File로 변환
        File convertedFile = convertMultipartFileToFile(file);

        // 업로드할 S3 경로 설정
        String fileName = folder + "/" + file.getOriginalFilename();

        // S3에 파일 업로드
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(fileName)
                        .build(),
                Paths.get(convertedFile.getAbsolutePath())
        );

        // 업로드된 파일의 S3 URL 반환
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", BUCKET_NAME, fileName);
    }

    // S3에서 파일 다운로드 메서드
    public void downloadFile(String key, String downloadPath) {
        // S3에서 파일 가져오기
        s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .build(),
                Paths.get(downloadPath)
        );
    }

    // MultipartFile을 File로 변환하는 메서드
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }
}

