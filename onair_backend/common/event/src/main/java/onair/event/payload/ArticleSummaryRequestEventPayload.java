package onair.event.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import onair.event.EventPayload;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleSummaryRequestEventPayload implements EventPayload {
    private Long articleId;
    private String content;
}
