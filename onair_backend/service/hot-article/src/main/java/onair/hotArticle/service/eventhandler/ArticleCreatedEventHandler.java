package onair.hotArticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import onair.event.Event;
import onair.event.EventType;
import onair.event.payload.ArticleCreatedEventPayload;
import onair.hotArticle.repository.ArticleCreatedTimeRepository;
import onair.hotArticle.utils.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleCreatedEventHandler implements EventHandler<ArticleCreatedEventPayload> {
    private final ArticleCreatedTimeRepository articleCreatedTimeRepository;

    @Override
    public void handle(Event<ArticleCreatedEventPayload> event) {
        ArticleCreatedEventPayload payload = event.getPayload();

        articleCreatedTimeRepository.createOrUpdateCreatedTime(
                payload.getArticleId(),
                payload.getCreatedAt(),
                TimeCalculatorUtils.calculateDurationToMidnight()
        );
    }

    @Override
    public boolean supports(Event<ArticleCreatedEventPayload> event) {
        return EventType.ARTICLE_CREATED == event.getType();
    }

    @Override
    public Long findArticleId(Event<ArticleCreatedEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}
