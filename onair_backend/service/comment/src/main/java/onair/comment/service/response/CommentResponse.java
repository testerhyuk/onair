package onair.comment.service.response;

import lombok.Getter;
import lombok.ToString;
import onair.comment.entity.Comment;

import java.time.LocalDateTime;

@Getter
@ToString
public class CommentResponse {
    private String commentId;
    private String articleId;
    private String parentCommentId;
    private String userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private boolean deleted;

    public static CommentResponse from(Comment comment) {
        CommentResponse response = new CommentResponse();

        response.commentId = String.valueOf(comment.getCommentId());
        response.articleId = String.valueOf(comment.getArticleId());
        response.parentCommentId = String.valueOf(comment.getParentCommentId());
        response.userId = String.valueOf(comment.getUserId());
        response.content = comment.getContent();
        response.createdAt = comment.getCreatedAt();
        response.modifiedAt = comment.getModifiedAt();
        response.deleted = comment.isDeleted();

        return response;
    }
}
