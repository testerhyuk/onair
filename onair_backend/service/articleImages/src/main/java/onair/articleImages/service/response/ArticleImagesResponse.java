package onair.articleImages.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.articleImages.entity.ArticleImages;

@Getter
@ToString
public class ArticleImagesResponse {
    private Long articleImageId;
    private Long articleId;
    private Long userId;
    private String imageUrls;

    public static ArticleImagesResponse from(ArticleImages articleImages) {
        ArticleImagesResponse response = new ArticleImagesResponse();

        response.articleImageId = articleImages.getArticleImagesId();
        response.articleId = articleImages.getArticleId();
        response.userId = articleImages.getUserId();
        response.imageUrls = articleImages.getImagesUrl();

        return response;
    }
}
