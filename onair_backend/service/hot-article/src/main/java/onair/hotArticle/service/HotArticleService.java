package onair.hotArticle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import onair.event.Event;
import onair.event.EventPayload;
import onair.event.EventType;
import onair.hotArticle.client.ArticleClient;
import onair.hotArticle.repository.HotArticleListRepository;
import onair.hotArticle.service.eventhandler.EventHandler;
import onair.hotArticle.service.response.HotArticleResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class HotArticleService {
    private final ArticleClient articleClient;
    private final List<EventHandler> eventHandlers;
    private final HotArticleScoreUpdater hotArticleScoreUpdater;
    private final HotArticleListRepository hotArticleListRepository;

    public void handleEvent(Event<EventPayload> event) {
        EventHandler<EventPayload> eventHandler = findEventHandler(event);

        if (eventHandler == null) {
            return;
        }

        if (isArticleCreatedOrDeleted(event)) {
            eventHandler.handle(event);
        } else {
            hotArticleScoreUpdater.updateHotArticle(event, eventHandler);
        }
    }

    public List<HotArticleResponse> readAll(String dateStr) {
        return hotArticleListRepository.readAll(dateStr).stream()
                .map(articleClient::read)
                .filter(Objects::nonNull)
                .map(HotArticleResponse::from)
                .toList();
    }

    private EventHandler<EventPayload> findEventHandler(Event<EventPayload> event) {
        return eventHandlers.stream()
                .filter(eventHandler -> eventHandler.supports(event))
                .findAny()
                .orElse(null);
    }

    private boolean isArticleCreatedOrDeleted(Event<EventPayload> event) {
        return EventType.ARTICLE_CREATED == event.getType() || EventType.ARTICLE_DELETED == event.getType();
    }
}
