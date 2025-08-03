package onair.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import onair.event.payload.*;

@RequiredArgsConstructor
@Log4j2
@Getter
public enum EventType {
    ARTICLE_CREATED(ArticleCreatedEventPayload.class, Topic.ONAIR_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload.class, Topic.ONAIR_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload.class, Topic.ONAIR_ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload.class, Topic.ONAIR_COMMENT),
    COMMENT_UPDATED(CommentUpdatedEventPayload.class, Topic.ONAIR_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload.class, Topic.ONAIR_COMMENT),
    ARTICLE_LIKED(ArticleLikedEventPayload.class, Topic.ONAIR_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload.class, Topic.ONAIR_LIKE),
    ARTICLE_VIEWED(ArticleViewedEventPayload.class, Topic.ONAIR_VIEW),
    ARTICLE_SUMMARY_RESPONSE(ArticleSummaryResponseEventPayload.class, Topic.ONAIR_ARTICLE_SUMMARY_RESPONSE),
    ARTICLE_SUMMARY_REQUEST(ArticleSummaryRequestEventPayload.class, Topic.ONAIR_ARTICLE_SUMMARY_REQUEST)
    ;

    private final Class<? extends EventPayload> payloadClass;
    private final String topic;

    public static EventType from(String type) {
        try {
            return valueOf(type);
        } catch (Exception e) {
            log.error("[EventType.from] type = {}", type, e);

            return null;
        }
    }

    public static class Topic {
        public static final String ONAIR_ARTICLE = "onair-article";
        public static final String ONAIR_COMMENT = "onair-comment";
        public static final String ONAIR_LIKE = "onair-article-like";
        public static final String ONAIR_VIEW = "onair-article-view";
        public static final String ONAIR_ARTICLE_SUMMARY_RESPONSE = "onair-article-summary-response";
        public static final String ONAIR_ARTICLE_SUMMARY_REQUEST = "onair-article-summary-request";
    }
}
