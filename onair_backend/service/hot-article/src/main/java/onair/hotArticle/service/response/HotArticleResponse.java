package onair.hotArticle.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.hotArticle.client.ArticleClient;

import java.time.LocalDateTime;

@Getter
@ToString
public class HotArticleResponse {
    private Long articleId;
    private String title;
    private LocalDateTime createdAt;

    public static HotArticleResponse from(ArticleClient.ArticleResponse articleResponse) {
        HotArticleResponse response = new HotArticleResponse();

        response.articleId = articleResponse.getArticleId();
        response.title = articleResponse.getTitle();
        response.createdAt = articleResponse.getCreatedAt();

        return response;
    }
}
