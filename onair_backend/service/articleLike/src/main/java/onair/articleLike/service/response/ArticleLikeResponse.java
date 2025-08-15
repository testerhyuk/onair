package onair.articleLike.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.articleLike.entity.ArticleLike;

import java.time.LocalDateTime;

@Getter
@ToString
public class ArticleLikeResponse {
    private String articleLikeId;
    private String articleId;
    private String userId;
    private LocalDateTime createdAt;
    private boolean liked;

    public static ArticleLikeResponse from(ArticleLike articleLike) {
        ArticleLikeResponse response = new ArticleLikeResponse();

        if (articleLike != null) {
            response.articleLikeId = String.valueOf(articleLike.getArticleLikeId());
            response.articleId = String.valueOf(articleLike.getArticleId());
            response.userId = String.valueOf(articleLike.getUserId());
            response.createdAt = articleLike.getCreatedAt();
            response.liked = true;
        } else {
            response.liked = false;
        }

        return response;
    }
}
