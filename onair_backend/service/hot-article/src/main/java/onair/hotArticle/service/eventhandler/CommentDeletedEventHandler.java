package onair.hotArticle.service.eventhandler;

import lombok.RequiredArgsConstructor;
import onair.event.Event;
import onair.event.EventType;
import onair.event.payload.CommentDeletedEventPayload;
import onair.hotArticle.repository.ArticleCommentCountRepository;
import onair.hotArticle.utils.TimeCalculatorUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentDeletedEventHandler implements EventHandler<CommentDeletedEventPayload> {
    private final ArticleCommentCountRepository articleCommentCountRepository;

    @Override
    public void handle(Event<CommentDeletedEventPayload> event) {
        CommentDeletedEventPayload payload = event.getPayload();

        articleCommentCountRepository.createOrUpdateComment(
                payload.getArticleId(),
                payload.getCommentCount(),
                TimeCalculatorUtils.calculateDurationToMidnight()
        );
    }

    @Override
    public boolean supports(Event<CommentDeletedEventPayload> event) {
        return EventType.COMMENT_DELETED == event.getType();
    }

    @Override
    public Long findArticleId(Event<CommentDeletedEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}
