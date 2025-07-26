package onair.comment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    private Long commentId;
    private Long articleId;
    private Long parentCommentId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean deleted;

    public static Comment create(Long commentId, Long articleId, Long parentCommentId, Long userId, String content) {
        Comment comment = new Comment();

        comment.commentId = commentId;
        comment.articleId = articleId;
        comment.parentCommentId = parentCommentId == null ? commentId : parentCommentId;
        comment.userId = userId;
        comment.content = content;
        comment.createdAt = LocalDateTime.now();
        comment.modifiedAt = comment.createdAt;
        comment.deleted = false;

        return comment;
    }

    public void update(String content) {
        this.content = content;
        this.modifiedAt = LocalDateTime.now();
    }

    public boolean isRoot() {
        return parentCommentId.longValue() == commentId;
    }

    public void delete() {
        deleted = true;
    }
}
