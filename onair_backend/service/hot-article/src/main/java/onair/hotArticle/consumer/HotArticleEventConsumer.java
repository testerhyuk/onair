package onair.hotArticle.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import onair.event.Event;
import onair.event.EventPayload;
import onair.event.EventType;
import onair.hotArticle.service.HotArticleService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class HotArticleEventConsumer {
    private final HotArticleService hotArticleService;

    @KafkaListener(topics = {
            EventType.Topic.ONAIR_ARTICLE,
            EventType.Topic.ONAIR_COMMENT,
            EventType.Topic.ONAIR_LIKE,
            EventType.Topic.ONAIR_VIEW
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[HotArticleEventConsumer.listen] received message {}", message);

        Event<EventPayload> event = Event.fromJson(message);

        if (event != null) {
            hotArticleService.handleEvent(event);
        }

        ack.acknowledge();
    }
}
