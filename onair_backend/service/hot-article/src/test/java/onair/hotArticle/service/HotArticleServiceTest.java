package onair.hotArticle.service;

import onair.event.Event;
import onair.event.EventType;
import onair.hotArticle.service.eventhandler.EventHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotArticleServiceTest {
    @InjectMocks
    HotArticleService hotArticleService;

    @Mock
    List<EventHandler> eventHandlers;

    @Mock
    HotArticleScoreUpdater hotArticleScoreUpdater;

    @Test
    void handleEventIfEventHandlerNotFoundTest() {
        Event event = mock(Event.class);
        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandler.supports(event)).willReturn(false);
        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

        hotArticleService.handleEvent(event);

        verify(eventHandler, never()).handle(event);
        verify(hotArticleScoreUpdater, never()).updateHotArticle(event, eventHandler);
    }

    @Test
    void handleEventIfArticleCreatedEventTest() {
        Event event = mock(Event.class);

        given(event.getType()).willReturn(EventType.ARTICLE_CREATED);

        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandler.supports(event)).willReturn(true);
        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

        hotArticleService.handleEvent(event);

        verify(eventHandler).handle(event);
        verify(hotArticleScoreUpdater, never()).updateHotArticle(event, eventHandler);
    }

    @Test
    void handleEventIfArticleDeletedEventTest() {
        Event event = mock(Event.class);

        given(event.getType()).willReturn(EventType.ARTICLE_DELETED);

        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandler.supports(event)).willReturn(true);
        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

        hotArticleService.handleEvent(event);

        verify(eventHandler).handle(event);
        verify(hotArticleScoreUpdater, never()).updateHotArticle(event, eventHandler);
    }

    @Test
    void handleEventIfScoreUpdatableEventTest() {
        Event event = mock(Event.class);

        given(event.getType()).willReturn(mock(EventType.class));

        EventHandler eventHandler = mock(EventHandler.class);

        given(eventHandler.supports(event)).willReturn(true);
        given(eventHandlers.stream()).willReturn(Stream.of(eventHandler));

        hotArticleService.handleEvent(event);

        verify(eventHandler, never()).handle(event);
        verify(hotArticleScoreUpdater).updateHotArticle(event, eventHandler);
    }
}