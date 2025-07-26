package onair.articleLike.entity;

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
public class ArticleLike {
    @Id
    private Long articleLikeId;
    private Long articleId;
    private Long userId;
    private LocalDateTime createdAt;

    public static ArticleLike create(Long articleLikeId, Long articleId, Long userId) {
        ArticleLike articleLike = new ArticleLike();

        articleLike.articleLikeId = articleLikeId;
        articleLike.articleId = articleId;
        articleLike.userId = userId;
        articleLike.createdAt = LocalDateTime.now();

        return articleLike;
    }
}
