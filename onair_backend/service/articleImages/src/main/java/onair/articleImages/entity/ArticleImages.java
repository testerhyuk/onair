package onair.articleImages.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class ArticleImages {
    @Id
    private Long articleImagesId;
    private Long articleId;
    private Long userId;
    private String imagesUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ArticleImages upload(Long articleImagesId, Long articleId, Long userId, String imagesUrl) {
        ArticleImages articleImages = new ArticleImages();

        articleImages.articleImagesId = articleImagesId;
        articleImages.articleId = articleId;
        articleImages.userId = userId;
        articleImages.imagesUrl = imagesUrl;
        articleImages.createdAt = LocalDateTime.now();
        articleImages.updatedAt = articleImages.createdAt;

        return articleImages;
    }

    public void update(String imagesUrl) {
        this.imagesUrl = imagesUrl;
        this.updatedAt = LocalDateTime.now();
    }
}
