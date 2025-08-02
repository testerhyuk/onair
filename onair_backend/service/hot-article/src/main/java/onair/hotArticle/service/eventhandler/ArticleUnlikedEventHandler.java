package onair.hotArticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import onair.event.Event;
import onair.event.EventType;
import onair.event.payload.ArticleUnlikedEventPayload;
import onair.hotArticle.repository.ArticleLikeCountRepository;
import onair.hotArticle.utils.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleUnlikedEventHandler implements EventHandler<ArticleUnlikedEventPayload> {
    private final ArticleLikeCountRepository articleLikeCountRepository;

    @Override
    public void handle(Event<ArticleUnlikedEventPayload> event) {
        ArticleUnlikedEventPayload payload = event.getPayload();

        articleLikeCountRepository.createOrUpdateLike(
                payload.getArticleId(),
                payload.getLikeCount(),
                TimeCalculatorUtils.calculateDurationToMidnight()
        );
    }



    @Override
    public boolean supports(Event<ArticleUnlikedEventPayload> event) {
        return EventType.ARTICLE_UNLIKED == event.getType();
    }

    @Override
    public Long findArticleId(Event<ArticleUnlikedEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}
