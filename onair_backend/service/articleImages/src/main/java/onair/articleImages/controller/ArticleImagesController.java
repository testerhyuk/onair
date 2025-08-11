package onair.articleImages.controller;

import lombok.RequiredArgsConstructor;
import onair.articleImages.service.ArticleImagesService;
import onair.articleImages.service.S3Service;
import onair.articleImages.service.request.ArticleImagesDeleteRequest;
import onair.articleImages.service.request.ArticleImagesUpdateRequest;
import onair.articleImages.service.request.ArticleImagesUploadRequest;
import onair.articleImages.service.response.ArticleImagesResponse;
import onair.articleImages.service.response.PreSignedUrlListResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleImagesController {
    private final S3Service s3Service;
    private final ArticleImagesService articleImagesService;

    @PostMapping("/v1/article-images/presigned-urls")
    public PreSignedUrlListResponse getPreSignedUrls(@RequestBody List<String> filenames) {
        return new PreSignedUrlListResponse(s3Service.createPreSignedUrls(filenames));
    }

    @PostMapping("/v1/article-images")
    public List<ArticleImagesResponse> upload(@RequestBody ArticleImagesUploadRequest request) {
        return articleImagesService.uploadImages(request);
    }

    @PutMapping("/v1/article-images")
    public List<ArticleImagesResponse> update(@RequestBody ArticleImagesUpdateRequest request) {
        return articleImagesService.updateImages(request);
    }

    @DeleteMapping("/v1/article-images")
    public List<ArticleImagesResponse> deleteImages(@RequestBody ArticleImagesDeleteRequest request) {
        return articleImagesService.deleteImages(request.getImageIds());
    }
}
