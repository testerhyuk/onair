package onair.hotArticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import onair.event.Event;
import onair.event.EventType;
import onair.event.payload.ArticleLikedEventPayload;
import onair.hotArticle.repository.ArticleLikeCountRepository;
import onair.hotArticle.utils.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleLikedEventHandler implements EventHandler<ArticleLikedEventPayload> {
    private final ArticleLikeCountRepository articleLikeCountRepository;

    @Override
    public void handle(Event<ArticleLikedEventPayload> event) {
        ArticleLikedEventPayload payload = event.getPayload();

        articleLikeCountRepository.createOrUpdateLike(
                payload.getArticleId(),
                payload.getLikeCount(),
                TimeCalculatorUtils.calculateDurationToMidnight()
        );
    }

    @Override
    public boolean supports(Event<ArticleLikedEventPayload> event) {
        return EventType.ARTICLE_LIKED == event.getType();
    }

    @Override
    public Long findArticleId(Event<ArticleLikedEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}
