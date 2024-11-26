package com.example.find_it.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private static final String BUCKET_NAME = "capstonem"; // S3 버킷 이름

    /**
     * Base64 이미지 업로드 메서드
     *
     * @param base64Image Base64로 인코딩된 이미지 데이터
     * @param folder 업로드할 S3 폴더 경로
     * @param fileName S3에 저장할 파일 이름
     * @return 업로드된 파일의 S3 URL
     */
    public String uploadFile(String base64Image, String folder, String fileName) {
        try {
            // Base64 데이터에서 헤더 제거 및 디코딩
            String base64Data = base64Image.split(",")[1]; // "data:image/jpeg;base64," 제거
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);

            // S3 경로 설정
            String key = folder + "/" + fileName;

            // S3에 파일 업로드
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(BUCKET_NAME)
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(imageBytes)
            );

            // 업로드된 파일의 S3 URL 반환
            return generateS3Url(key);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 image format", e);
        }
    }

    /**
     * S3에서 파일 다운로드 메서드
     *
     * @param key S3 버킷 내의 파일 키
     * @param downloadPath 다운로드할 로컬 경로
     */
    public void downloadFile(String key, String downloadPath) {
        s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .build(),
                Paths.get(downloadPath)
        );
    }

    /**
     * S3 URL 생성 메서드
     *
     * @param fileName S3 키
     * @return 파일의 S3 URL
     */
    private String generateS3Url(String fileName) {
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", BUCKET_NAME, fileName);
    }
}
