package onair.articleLike.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ArticleLikeCount {
    @Id
    private Long articleId;
    private Long likeCount;

    public static ArticleLikeCount init(Long articleId, Long likeCount) {
        ArticleLikeCount articleLikeCount = new ArticleLikeCount();

        articleLikeCount.articleId = articleId;
        articleLikeCount.likeCount = likeCount;

        return articleLikeCount;
    }

    public void increase() {
        this.likeCount++;
    }

    public void decrease() {
        this.likeCount--;
    }
}
