package onair.articleLike.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.articleLike.entity.ArticleLike;

import java.time.LocalDateTime;

@Getter
@ToString
public class ArticleLikeResponse {
    private Long articleLikeId;
    private Long articleId;
    private Long userId;
    private LocalDateTime createdAt;
    private boolean liked;

    public static ArticleLikeResponse from(ArticleLike articleLike) {
        ArticleLikeResponse response = new ArticleLikeResponse();

        if (articleLike != null) {
            response.articleLikeId = articleLike.getArticleLikeId();
            response.articleId = articleLike.getArticleId();
            response.userId = articleLike.getUserId();
            response.createdAt = articleLike.getCreatedAt();
            response.liked = true;
        } else {
            response.liked = false;
        }

        return response;
    }
}
