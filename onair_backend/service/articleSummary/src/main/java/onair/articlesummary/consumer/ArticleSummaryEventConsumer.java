package onair.articlesummary.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import onair.articlesummary.service.ArticleSummaryService;
import onair.event.Event;
import onair.event.EventPayload;
import onair.event.EventType;
import onair.event.payload.ArticleSummaryResponseEventPayload;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class ArticleSummaryEventConsumer {
    private final ArticleSummaryService articleSummaryService;

    @KafkaListener(topics = EventType.Topic.ONAIR_ARTICLE_SUMMARY_RESPONSE)
    public void listen(String message, Acknowledgment ack) {
        try {
            log.info("[ArticleSummaryEventConsumer.listen] received message {}", message);

            Event<EventPayload> event = Event.fromJson(message);

            ArticleSummaryResponseEventPayload payload = (ArticleSummaryResponseEventPayload) event.getPayload();

            articleSummaryService.save(payload.getArticleId(), payload.getSummary());

            ack.acknowledge();
        } catch (Exception e) {
            log.error("[ArticleSummaryEventConsumer.listen] error processing message", e);
        }
    }
}
