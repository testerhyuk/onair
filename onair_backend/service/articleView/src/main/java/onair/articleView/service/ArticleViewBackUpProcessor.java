package onair.articleView.service;

import lombok.RequiredArgsConstructor;
import onair.articleView.entity.ArticleView;
import onair.articleView.repository.ArticleViewBackUpRepository;
import onair.event.EventType;
import onair.event.payload.ArticleLikedEventPayload;
import onair.event.payload.ArticleViewedEventPayload;
import onair.outboxmessagerelay.OutboxEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArticleViewBackUpProcessor {
    private final ArticleViewBackUpRepository articleViewBackUpRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public void backup(Long articleId, Long viewCount) {
        int result = articleViewBackUpRepository.updateViewCount(articleId, viewCount);

        if (result == 0) {
            articleViewBackUpRepository.findById(articleId)
                    .ifPresentOrElse(ignored -> {},
                            () -> articleViewBackUpRepository.save(
                                    ArticleView.init(articleId, viewCount)
                            ));
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_VIEWED,
                ArticleViewedEventPayload.builder()
                        .articleId(articleId)
                        .viewCount(viewCount)
                        .build(),
                articleId
        );
    }
}
