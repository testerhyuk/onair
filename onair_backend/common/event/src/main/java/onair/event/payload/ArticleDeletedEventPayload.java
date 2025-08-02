package onair.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import onair.event.EventPayload;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDeletedEventPayload implements EventPayload {
    private Long articleId;
    private Long boardId;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long articleCount;
}
