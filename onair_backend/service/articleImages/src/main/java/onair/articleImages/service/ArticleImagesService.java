package onair.articleImages.service;

import lombok.RequiredArgsConstructor;
import onair.articleImages.entity.ArticleImages;
import onair.articleImages.repository.ArticleImagesRepository;
import onair.articleImages.service.request.ArticleImagesUpdateRequest;
import onair.articleImages.service.request.ArticleImagesUploadRequest;
import onair.articleImages.service.response.ArticleImagesResponse;
import onair.snowflake.Snowflake;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleImagesService {
    private final ArticleImagesRepository articleImagesRepository;
    private final Snowflake snowflake = new Snowflake();
    private final S3Service s3Service;

    @Transactional
    public List<ArticleImagesResponse> uploadImages(ArticleImagesUploadRequest request) {
        return request.getImageUrls().stream()
                .map(url -> {
                    ArticleImages articleImages = ArticleImages.upload(
                            snowflake.nextId(),
                            request.getArticleId(),
                            request.getUserId(),
                            url
                    );

                    articleImagesRepository.save(articleImages);

                    return ArticleImagesResponse.from(articleImages);
                }).collect(Collectors.toList());
    }

    @Transactional
    public List<ArticleImagesResponse> updateImages(ArticleImagesUpdateRequest request) {
        Long articleId = request.getArticleId();
        Long userId = request.getUserId();

        List<ArticleImages> currentImages = articleImagesRepository.findByArticleId(articleId);
        List<String> remainingImages = request.getRemainingImageIds();

        List<ArticleImages> toDelete = currentImages.stream()
                .filter(img -> !remainingImages.contains(img.getImagesUrl()))
                .toList();

        toDelete.forEach(articleImagesRepository::delete);

        List<ArticleImagesResponse> newSaved = request.getNewImageUrls().stream()
                .map(url -> {
                    ArticleImages articleImages = ArticleImages.upload(
                            snowflake.nextId(),
                            articleId,
                            userId,
                            url
                    );

                    articleImagesRepository.save(articleImages);

                    return ArticleImagesResponse.from(articleImages);
                }).toList();

        List<ArticleImagesResponse> remainingResponses = currentImages.stream()
                .filter(img -> remainingImages.contains(img.getImagesUrl()))
                .map(ArticleImagesResponse::from)
                .collect(Collectors.toList());

        remainingResponses.addAll(newSaved);

        return remainingResponses;
    }

    @Transactional
    public List<ArticleImagesResponse> deleteImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return Collections.emptyList();
        }

        List<ArticleImages> imagesToDelete = articleImagesRepository.findByImagesUrlIn(imageUrls);


        if (imagesToDelete.isEmpty()) {
            return Collections.emptyList();
        }

        for (ArticleImages image : imagesToDelete) {
            s3Service.deleteImages(image.getImagesUrl());
        }

        articleImagesRepository.deleteAll(imagesToDelete);

        if (imagesToDelete.isEmpty()) {
            return Collections.emptyList();
        }


        return imagesToDelete.stream()
                .map(ArticleImagesResponse::from)
                .collect(Collectors.toList());
    }

    public List<String> getImageUrlsByArticleId(Long articleId) {
        return articleImagesRepository.findByArticleId(articleId).stream()
                .map(ArticleImages::getImagesUrl)
                .toList();
    }
}
