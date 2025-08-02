package onair.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import onair.event.EventPayload;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleViewedEventPayload implements EventPayload {
    private Long articleId;
    private Long viewCount;
}
