package onair.article.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.article.entity.Article;
import onair.article.entity.Category;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class ArticleResponse {
    private Long articleId;
    private Long boardId;
    private Long userId;
    private String title;
    private String content;
    private Category category;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ArticleResponse from(Article article) {
        ArticleResponse response = new ArticleResponse();

        response.articleId = article.getArticleId();
        response.boardId = article.getBoardId();
        response.userId = article.getUserId();
        response.title = article.getTitle();
        response.content = article.getContent();
        response.category = article.getCategory();
        response.createdAt = article.getCreatedAt();
        response.modifiedAt = article.getModifiedAt();

        return response;
    }
}
