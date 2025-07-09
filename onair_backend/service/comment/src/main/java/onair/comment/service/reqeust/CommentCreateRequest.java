package onair.comment.service.reqeust;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CommentCreateRequest {
    private Long articleId;
    private Long parentCommentId;
    private Long userId;
    private String content;
}
