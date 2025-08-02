package onair.event;

import lombok.extern.log4j.Log4j2;
import onair.event.payload.ArticleCreatedEventPayload;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
class EventTest {
    @Test
    void serde() {
        ArticleCreatedEventPayload payload = ArticleCreatedEventPayload.builder()
                .articleId(1001L)
                .title("title")
                .content("content")
                .boardId(2001L)
                .userId(2001L)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .articleCount(23L)
                .build();

        Event<EventPayload> event = Event.of(
                1234L,
                EventType.ARTICLE_CREATED,
                payload
        );

        String json = event.toJson();

        log.info("json = %s".formatted(json));

        Event<EventPayload> result = Event.fromJson(json);

        assertThat(result.getEventId()).isEqualTo(event.getEventId());
        assertThat(result.getType()).isEqualTo(event.getType());
        assertThat(result.getPayload()).isInstanceOf(payload.getClass());

        ArticleCreatedEventPayload resultPayload = (ArticleCreatedEventPayload) result.getPayload();

        assertThat(resultPayload.getArticleId()).isEqualTo(payload.getArticleId());
        assertThat(resultPayload.getTitle()).isEqualTo(payload.getTitle());
        assertThat(resultPayload.getCreatedAt()).isEqualTo(payload.getCreatedAt());
        assertThat(resultPayload.getArticleCount()).isEqualTo(payload.getArticleCount());
    }
}