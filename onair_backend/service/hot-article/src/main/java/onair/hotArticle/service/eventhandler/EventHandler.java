package onair.hotArticle.service.eventhandler;

import onair.event.Event;
import onair.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
    Long findArticleId(Event<T> event);
}
