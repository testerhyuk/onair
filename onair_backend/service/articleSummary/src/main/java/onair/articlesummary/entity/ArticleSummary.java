package onair.articlesummary.entity;

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
public class ArticleSummary {
    @Id
    private Long articleSummaryId;
    private Long articleId;
    private String summary;
    private LocalDateTime generatedAt;

    public static ArticleSummary create(Long articleSummaryId, Long articleId, String summary) {
        ArticleSummary articleSummary = new ArticleSummary();

        articleSummary.articleSummaryId = articleSummaryId;
        articleSummary.articleId = articleId;
        articleSummary.summary = summary;
        articleSummary.generatedAt = LocalDateTime.now();

        return articleSummary;
    }
}
