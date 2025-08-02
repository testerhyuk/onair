package onair.outboxmessagerelay;

import lombok.RequiredArgsConstructor;
import onair.event.Event;
import onair.event.EventPayload;
import onair.event.EventType;
import onair.snowflake.Snowflake;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {
    private final Snowflake outboxIdSnowflake = new Snowflake();
    private final Snowflake eventIdSnowflake = new Snowflake();
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(EventType type, EventPayload payload, Long shardKey) {
        Outbox outbox = Outbox.create(
                outboxIdSnowflake.nextId(),
                type,
                Event.of(eventIdSnowflake.nextId(), type, payload).toJson(),
                shardKey % MessageRelayConstants.SHARD_COUNT
        );

        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox));
    }
}
