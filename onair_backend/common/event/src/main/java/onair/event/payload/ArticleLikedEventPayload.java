package onair.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import onair.event.EventPayload;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleLikedEventPayload implements EventPayload {
    private Long articleLikeId;
    private Long articleId;
    private Long userId;
    private LocalDateTime createdAt;
    private Long likeCount;
}
