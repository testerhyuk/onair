package onair.articleView.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class ArticleView {
    @Id
    private Long articleId;
    private Long viewCount;

    public static ArticleView init(Long articleId, Long viewCount) {
        ArticleView articleView = new ArticleView();

        articleView.articleId = articleId;
        articleView.viewCount = viewCount;

        return articleView;
    }
}
