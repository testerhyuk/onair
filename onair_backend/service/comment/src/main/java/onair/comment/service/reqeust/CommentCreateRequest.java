package onair.comment.service.reqeust;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CommentCreateRequest {
    private String articleId;
    private String parentCommentId;
    private String userId;
    private String content;
}
