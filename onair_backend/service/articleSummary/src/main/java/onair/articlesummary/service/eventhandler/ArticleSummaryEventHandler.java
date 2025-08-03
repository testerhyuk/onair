package onair.articlesummary.service.eventhandler;

import lombok.RequiredArgsConstructor;
import onair.articlesummary.service.ArticleSummaryService;
import onair.event.Event;
import onair.event.EventType;
import onair.event.payload.ArticleSummaryResponseEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleSummaryEventHandler implements EventHandler<ArticleSummaryResponseEventPayload> {
    private final ArticleSummaryService articleSummaryService;

    @Override
    public void handle(Event<ArticleSummaryResponseEventPayload> event) {
        ArticleSummaryResponseEventPayload payload = event.getPayload();
        Long articleId = payload.getArticleId();
        String summary = payload.getSummary();

        articleSummaryService.save(articleId, summary);
    }

    @Override
    public boolean supports(Event<ArticleSummaryResponseEventPayload> event) {
        return EventType.ARTICLE_SUMMARY_RESPONSE == event.getType();
    }

    @Override
    public Long findArticleId(Event<ArticleSummaryResponseEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}
