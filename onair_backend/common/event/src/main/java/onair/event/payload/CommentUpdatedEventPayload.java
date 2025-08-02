package onair.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import onair.event.EventPayload;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class CommentUpdatedEventPayload implements EventPayload {
    private Long commentId;
    private Long articleId;
    private Long parentCommentId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long commentCount;
}
