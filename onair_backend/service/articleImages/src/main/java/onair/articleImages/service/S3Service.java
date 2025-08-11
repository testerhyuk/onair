package onair.articleImages.service;

import lombok.RequiredArgsConstructor;
import onair.articleImages.service.response.PreSignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3Service {
    @Value("${bucket.name}")
    private String bucket;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    public List<PreSignedUrlResponse> createPreSignedUrls(List<String> fileNames) {
        return fileNames.stream()
                .map(fileName -> new PreSignedUrlResponse(fileName, generatePreSignedUrl(fileName)))
                .toList();
    }

    private String generatePreSignedUrl(String fileName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    public void deleteImages(String imageUrl) {
        String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}
