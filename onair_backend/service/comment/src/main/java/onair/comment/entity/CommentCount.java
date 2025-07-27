package onair.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentCount {
    @Id
    private Long articleId;
    private Long articleCommentCount;

    public static CommentCount init(Long articleId, Long articleCommentCount) {
        CommentCount commentCount = new CommentCount();

        commentCount.articleId = articleId;
        commentCount.articleCommentCount = articleCommentCount;

        return commentCount;
    }
}
