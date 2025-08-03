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
public class ArticleSummaryResponseEventPayload implements EventPayload {
    private Long articleId;
    private String summary;
    private LocalDateTime generatedAt;
}
