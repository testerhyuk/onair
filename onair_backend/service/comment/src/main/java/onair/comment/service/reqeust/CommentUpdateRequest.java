package onair.comment.service.reqeust;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class CommentUpdateRequest {
    private String comment;
}
