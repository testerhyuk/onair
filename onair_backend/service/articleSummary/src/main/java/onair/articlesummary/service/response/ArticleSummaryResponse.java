package onair.articlesummary.service.response;

import lombok.Getter;
import onair.articlesummary.entity.ArticleSummary;

import java.time.LocalDateTime;

@Getter
public class ArticleSummaryResponse {
    private String articleId;
    private String summary;
    private LocalDateTime generatedAt;

    public static ArticleSummaryResponse from(ArticleSummary articleSummary) {
        ArticleSummaryResponse response = new ArticleSummaryResponse();

        response.articleId = String.valueOf(articleSummary.getArticleId());
        response.summary = articleSummary.getSummary();
        response.generatedAt = articleSummary.getGeneratedAt();

        return response;
    }
}
