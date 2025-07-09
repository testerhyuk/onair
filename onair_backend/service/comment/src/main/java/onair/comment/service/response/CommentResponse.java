package onair.comment.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.comment.entity.Comment;

import java.time.LocalDateTime;

@Getter
@ToString
public class CommentResponse {
    private Long commentId;
    private Long articleId;
    private Long parentCommentId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean deleted;

    public static CommentResponse from(Comment comment) {
        CommentResponse response = new CommentResponse();

        response.commentId = comment.getCommentId();
        response.articleId = comment.getArticleId();
        response.parentCommentId = comment.getParentCommentId();
        response.userId = comment.getUserId();
        response.content = comment.getContent();
        response.createdAt = comment.getCreatedAt();
        response.modifiedAt = comment.getModifiedAt();
        response.deleted = comment.isDeleted();

        return response;
    }
}
